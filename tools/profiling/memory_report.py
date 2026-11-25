#!/usr/bin/env python3
"""Build report.md from a memory-cycles.sh output directory.

Checks are advisory: each budget overrun prints a GitHub `::warning::` and is marked in the
report, and the script always exits 0. Needs the `perfetto` package (pip install perfetto).

Usage: tools/profiling/memory_report.py <out-dir>
"""
import os
import re
import sys
from pathlib import Path

from perfetto.trace_processor import TraceProcessor

# Objects of these classes still reachable on Goodbye mean a finished call is kept alive.
CALL_CLASSES = [
    "com.vonage.android.meetingroom.internal.viewmodel.MeetingRoomViewModel",
    "com.vonage.android.meetingroom.internal.container.MeetingRoomContainer",
    "com.vonage.android.screen.waiting.WaitingRoomViewModel",
    "com.vonage.android.kotlin.Call",
    "com.opentok.android.Session",
    "com.opentok.android.Publisher",
    "com.opentok.android.Subscriber",
    "com.opentok.android.grafika.gles.EglCore",
    "android.view.TextureView",
]
MAX_JAVA_HEAP_GROWTH_MB = 10
MAX_PSS_GROWTH_PER_CYCLE_MB = 5
MEMINFO_ROWS = ["TOTAL PSS", "Java Heap", "Native Heap", "Graphics"]
MAX_PATH_STEPS = 40

warnings = []


def warn(message):
    warnings.append(message)
    print(f"::warning title=Memory profiling::{message}")


def reachable_mb(tp):
    row = next(iter(tp.query("SELECT SUM(self_size) AS size FROM heap_graph_object WHERE reachable")))
    return (row.size or 0) / 1e6


def retained_counts(tp):
    names = ", ".join(f"'{name}'" for name in CALL_CLASSES)
    rows = tp.query(
        "SELECT c.name AS cls, COUNT(*) AS n, MIN(o.id) AS first_id "
        "FROM heap_graph_object o JOIN heap_graph_class c ON o.type_id = c.id "
        f"WHERE o.reachable AND c.name IN ({names}) GROUP BY c.name"
    )
    return {row.cls: (row.n, row.first_id) for row in rows}


def retention_path(tp, object_id):
    """Dominator chain from the GC root side down to the object: each entry keeps the next alive."""
    steps = []
    current = object_id
    for _ in range(MAX_PATH_STEPS):
        rows = list(tp.query(
            "SELECT d.idom_id AS idom, c.name AS cls, o.root_type AS root "
            "FROM heap_graph_dominator_tree d "
            "JOIN heap_graph_object o ON o.id = d.id JOIN heap_graph_class c ON c.id = o.type_id "
            f"WHERE d.id = {current}"
        ))
        if not rows:
            break
        row = rows[0]
        steps.append(row.cls + (f" [{row.root}]" if row.root else ""))
        if row.idom is None:
            break
        current = row.idom
    return list(reversed(steps))


def parse_meminfo(path):
    values = {}
    for line in path.read_text().splitlines():
        match = re.match(r"\s*(TOTAL PSS|Java Heap|Native Heap|Graphics):\s+(\d+)", line)
        if match and match.group(1) not in values:
            values[match.group(1)] = int(match.group(2)) / 1024
    return values


def main():
    if len(sys.argv) != 2:
        sys.exit(__doc__)
    out = Path(sys.argv[1])
    lines = ["# Memory profiling report", ""]

    with TraceProcessor(trace=str(out / "heap_baseline.perfetto-trace")) as tp:
        baseline_mb = reachable_mb(tp)
    with TraceProcessor(trace=str(out / "heap_final.perfetto-trace")) as tp:
        final_mb = reachable_mb(tp)
        tp.query("INCLUDE PERFETTO MODULE android.memory.heap_graph.dominator_tree")
        retained = retained_counts(tp)
        paths = {cls: retention_path(tp, first_id) for cls, (_, first_id) in retained.items()}

    lines += ["## Call objects reachable on Goodbye", "", "| Class | Instances |", "|---|---|"]
    for cls in CALL_CLASSES:
        count = retained.get(cls, (0, None))[0]
        lines.append(f"| `{cls}` | {count}{' ⚠️' if count else ''} |")
        if count:
            warn(f"{count} {cls.rsplit('.', 1)[-1]} still reachable on Goodbye")
    for cls, path in paths.items():
        lines += ["", f"Dominator chain to `{cls.rsplit('.', 1)[-1]}` (GC root side first):", "", "```", *path, "```"]

    growth = final_mb - baseline_mb
    lines += ["", "## Java heap (reachable)", "",
              f"Baseline {baseline_mb:.1f} MB, final {final_mb:.1f} MB, growth {growth:+.1f} MB "
              f"(budget {MAX_JAVA_HEAP_GROWTH_MB} MB){' ⚠️' if growth > MAX_JAVA_HEAP_GROWTH_MB else ''}"]
    if growth > MAX_JAVA_HEAP_GROWTH_MB:
        warn(f"Reachable Java heap grew {growth:.1f} MB over the run")

    samples = sorted(out.glob("meminfo_*.txt"), key=lambda p: int(p.stem.split("_")[1]))
    table = [(int(p.stem.split("_")[1]), parse_meminfo(p)) for p in samples]
    lines += ["", "## dumpsys meminfo (MB)", "",
              "Sample 0 is Landing; the last sample is Goodbye; the others are the waiting room after each call.",
              "", "| Sample | " + " | ".join(MEMINFO_ROWS) + " |", "|---" * (len(MEMINFO_ROWS) + 1) + "|"]
    for index, values in table:
        lines.append(f"| {index} | " + " | ".join(f"{values.get(k, 0):.1f}" for k in MEMINFO_ROWS) + " |")
    # Only waiting-room samples are comparable with each other.
    waiting = [values for index, values in table if 0 < index < len(table) - 1]
    if len(waiting) >= 2:
        per_cycle = (waiting[-1].get("TOTAL PSS", 0) - waiting[0].get("TOTAL PSS", 0)) / (len(waiting) - 1)
        over = per_cycle > MAX_PSS_GROWTH_PER_CYCLE_MB
        lines += ["", f"TOTAL PSS growth per cycle: {per_cycle:+.1f} MB "
                      f"(budget {MAX_PSS_GROWTH_PER_CYCLE_MB} MB){' ⚠️' if over else ''}"]
        if over:
            warn(f"TOTAL PSS grows {per_cycle:.1f} MB per call cycle")

    lines += ["", "## Summary", ""]
    lines += [f"- ⚠️ {w}" for w in warnings] or ["- No budget exceeded."]
    lines += ["", "Traces: `heap_baseline`, `heap_final` and `memory_counters` (.perfetto-trace), "
                  "openable in https://ui.perfetto.dev."]

    report = "\n".join(lines) + "\n"
    (out / "report.md").write_text(report)
    summary = os.environ.get("GITHUB_STEP_SUMMARY")
    if summary:
        with open(summary, "a") as handle:
            handle.write(report)
    print(report)


if __name__ == "__main__":
    main()
