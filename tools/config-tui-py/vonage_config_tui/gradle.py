"""Runs Gradle tasks."""

from __future__ import annotations

import subprocess
import sys
import time
from dataclasses import dataclass
from pathlib import Path
from typing import Callable, Optional

def _find_project_root() -> Path:
    """Locate the repo root by walking upward from cwd until gradlew is found.

    Falls back to the __file__-relative path so that ``python -m vonage_config_tui``
    from the repo root still works without an install.
    """
    for parent in [Path.cwd(), *Path.cwd().parents]:
        if (parent / "gradlew").exists() or (parent / "gradlew.bat").exists():
            return parent
    # fallback: __file__-relative (works when running from repo without install)
    return Path(__file__).resolve().parents[3]


PROJECT_ROOT: Path = _find_project_root()

ProgressCallback = Callable[[str], None]


@dataclass
class GradleResult:
    success: bool
    output: str
    duration_ms: int


def run_gradle_task(task: str, on_progress: Optional[ProgressCallback] = None) -> GradleResult:
    """Run a Gradle task from the project root, streaming output through ``on_progress``.

    Supported ``task`` values match the TS version:
        - "generateVonageConfig" → `./gradlew clean generateVonageConfig`
        - "generateTheme"        → `./gradlew clean :vonage-video-ui-compose:generateTheme`
        - anything else          → `./gradlew <task>`  (used for ``installDebug``)
    """
    if task == "generateTheme":
        args = ["./gradlew", "clean", ":vonage-video-ui-compose:generateTheme", "--console=plain"]
    elif task == "generateVonageConfig":
        args = ["./gradlew", "clean", "generateVonageConfig", "--console=plain"]
    else:
        args = ["./gradlew", task, "--console=plain"]

    if sys.platform == "win32":
        args[0] = "gradlew.bat"

    start = time.time()
    try:
        proc = subprocess.Popen(
            args,
            cwd=str(PROJECT_ROOT),
            stdout=subprocess.PIPE,
            stderr=subprocess.STDOUT,
            text=True,
            bufsize=1,
        )
    except OSError as e:
        return GradleResult(False, str(e), int((time.time() - start) * 1000))

    parts: list[str] = []
    assert proc.stdout is not None
    for raw in proc.stdout:
        parts.append(raw)
        if on_progress:
            stripped = raw.strip()
            if stripped:
                try:
                    on_progress(stripped)
                except Exception:
                    # Never let a UI callback abort the build read loop.
                    pass
    proc.wait()

    duration_ms = int((time.time() - start) * 1000)
    return GradleResult(proc.returncode == 0, "".join(parts).strip(), duration_ms)
