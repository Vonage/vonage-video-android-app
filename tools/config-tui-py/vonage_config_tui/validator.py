"""JSON Schema validation for config/app-config.json and config/theme.json.

Neither schema is committed to the repo: both are unified schemas shared with
the iOS and React Vonage Video apps, fetched at runtime from their canonical
URLs. A copy of each is cached on disk so the tool keeps working offline
after the first successful fetch.
"""

from __future__ import annotations

import json
import urllib.error
import urllib.request
from dataclasses import dataclass
from pathlib import Path
from typing import Any

from jsonschema import Draft202012Validator

APP_CONFIG_SCHEMA_URL = (
    "https://raw.githubusercontent.com/Vonage/vonage-video-react-app/"
    "40ff3e4b3ef83b315498d3dfe06f3cff91674cbc/specs/app-config.schema.json"
)
THEME_SCHEMA_URL = (
    "https://raw.githubusercontent.com/Vonage/vonage-video-react-app/"
    "40ff3e4b3ef83b315498d3dfe06f3cff91674cbc/specs/theme.schema.json"
)
_SCHEMA_FETCH_TIMEOUT_SECONDS = 10
_CACHE_DIR = Path(__file__).resolve().parent / ".cache"


def _find_config_root() -> Path:
    """Locate the repo's config/ directory.

    Walk upward from cwd until we find a directory that contains
    config/app-config.json (the definitive repo marker). Falls back to the
    __file__-relative path so that ``python -m vonage_config_tui`` from the
    repo root still works without an install.
    """
    for parent in [Path.cwd(), *Path.cwd().parents]:
        candidate = parent / "config" / "app-config.json"
        if candidate.exists():
            return parent / "config"
    # fallback: __file__-relative (works when running from repo without install)
    return Path(__file__).resolve().parents[3] / "config"


CONFIG_ROOT: Path = _find_config_root()


def _fetch_schema(url: str, cache_file: Path) -> dict[str, Any]:
    """Fetches a JSON schema from ``url``.

    Falls back to the last successfully fetched copy (cached under
    ``.cache/`` alongside this package) if the network is unavailable, so the
    TUI still works offline after the first run. Raises if neither the
    network nor a cache is available.
    """
    try:
        request = urllib.request.Request(url, headers={"User-Agent": "vonage-config-tui"})
        with urllib.request.urlopen(request, timeout=_SCHEMA_FETCH_TIMEOUT_SECONDS) as response:
            raw = response.read().decode("utf-8")
        schema = json.loads(raw)
        try:
            cache_file.parent.mkdir(parents=True, exist_ok=True)
            cache_file.write_text(raw, encoding="utf-8")
        except OSError:
            pass  # Caching is best-effort; a failed write shouldn't break validation.
        return schema
    except (urllib.error.URLError, TimeoutError, ValueError) as fetch_error:
        if cache_file.exists():
            return json.loads(cache_file.read_text(encoding="utf-8"))
        raise RuntimeError(
            f"Could not fetch schema from {url} and no cached copy is available. "
            "Connect to the network and retry."
        ) from fetch_error


APP_CONFIG_SCHEMA: dict[str, Any] = _fetch_schema(
    APP_CONFIG_SCHEMA_URL, _CACHE_DIR / "app-config.schema.json"
)
THEME_SCHEMA: dict[str, Any] = _fetch_schema(THEME_SCHEMA_URL, _CACHE_DIR / "theme.schema.json")

_app_validator = Draft202012Validator(APP_CONFIG_SCHEMA)
_theme_validator = Draft202012Validator(THEME_SCHEMA)


@dataclass
class ValidationResult:
    valid: bool
    errors: list[str]


def _format_errors(validator: Draft202012Validator, data: Any) -> ValidationResult:
    issues = list(validator.iter_errors(data))
    if not issues:
        return ValidationResult(True, [])
    formatted: list[str] = []
    for e in issues:
        path = "/" + "/".join(str(p) for p in e.absolute_path) if e.absolute_path else "/"
        formatted.append(f"{path}: {e.message}")
    return ValidationResult(False, formatted)


def validate_app_config_data(data: Any) -> ValidationResult:
    return _format_errors(_app_validator, data)


def validate_theme_data(data: Any) -> ValidationResult:
    return _format_errors(_theme_validator, data)


def load_app_config() -> tuple[dict[str, Any], ValidationResult]:
    raw = (CONFIG_ROOT / "app-config.json").read_text(encoding="utf-8")
    data = json.loads(raw)
    return data, validate_app_config_data(data)


def load_theme_config() -> tuple[dict[str, Any], ValidationResult]:
    raw = (CONFIG_ROOT / "theme.json").read_text(encoding="utf-8")
    data = json.loads(raw)
    return data, validate_theme_data(data)
