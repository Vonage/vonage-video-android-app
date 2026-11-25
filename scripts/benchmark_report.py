#!/usr/bin/env python3
"""Summarise Macrobenchmark results and warn on budget overruns.

Reads every *benchmarkData.json under the given directory, writes a markdown table to
$GITHUB_STEP_SUMMARY (or stdout) and prints a GitHub `::warning::` for each metric over
its budget. Always exits 0: budgets are advisory until they have settled.

Usage: scripts/benchmark_report.py <results-dir>
"""
import json
import os
import sys
from pathlib import Path

# Metric name prefix -> highest acceptable median. Medians above it produce a warning.
BUDGETS = {
    "idleLandingRecompose": 0,
    "idleLandingMeasureAndLayout": 0,
}


def budget_for(metric):
    return next((limit for prefix, limit in BUDGETS.items() if metric.startswith(prefix)), None)


def rows(results_dir):
    for path in sorted(Path(results_dir).rglob("*benchmarkData.json")):
        for bench in json.loads(path.read_text()).get("benchmarks", []):
            test = f"{bench['className'].rsplit('.', 1)[-1]}.{bench['name']}"
            for metric, data in bench.get("metrics", {}).items():
                yield test, metric, data["median"], budget_for(metric)
            for metric, data in bench.get("sampledMetrics", {}).items():
                yield test, f"{metric} P90", data["P90"], budget_for(metric)


def main():
    if len(sys.argv) != 2:
        sys.exit(__doc__)
    lines = ["## Benchmark results", "", "| Test | Metric | Median | Budget |", "|---|---|---|---|"]
    found = False
    for test, metric, value, budget in rows(sys.argv[1]):
        found = True
        over = budget is not None and value > budget
        lines.append(f"| {test} | {metric} | {value:g}{' ⚠️' if over else ''} | {'' if budget is None else budget} |")
        if over:
            print(f"::warning title=Benchmark budget::{test} {metric} median {value:g} > budget {budget}")
    if not found:
        print(f"::warning title=Benchmark results::no benchmarkData.json found under {sys.argv[1]}")
        return
    report = "\n".join(lines) + "\n"
    summary = os.environ.get("GITHUB_STEP_SUMMARY")
    if summary:
        with open(summary, "a") as out:
            out.write(report)
    else:
        print(report)


if __name__ == "__main__":
    main()
