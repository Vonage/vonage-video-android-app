"""JSON Schema validation for config/app-config.json and config/theme.json.

``app-config.schema.json`` is a local file. ``theme.schema.json`` is no longer
committed to the repo: it is the unified schema shared with the iOS and React
Vonage Video apps, fetched at runtime from ``THEME_SCHEMA_URL``. A copy is
cached on disk so the tool keeps working offline after the first successful
fetch.
"""

from __future__ import annotations

import json
import urllib.error
import urllib.request
from dataclasses import dataclass
from pathlib import Path
from typing import Any

from jsonschema import Draft202012Validator

THEME_SCHEMA_URL = (
    "https://raw.githubusercontent.com/Vonage/vonage-video-react-app/"
    "40ff3e4b3ef83b315498d3dfe06f3cff91674cbc/specs/theme.schema.json"
)
_SCHEMA_FETCH_TIMEOUT_SECONDS = 10


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
_THEME_SCHEMA_CACHE_FILE: Path = Path(__file__).resolve().parent / ".cache" / "theme.schema.json"


def _load_schema(filename: str) -> dict[str, Any]:
    return json.loads((CONFIG_ROOT / filename).read_text(encoding="utf-8"))


def _fetch_theme_schema() -> dict[str, Any]:
    """Fetches the unified theme schema from ``THEME_SCHEMA_URL``.

    Falls back to the last successfully fetched copy (cached alongside
    ``config/``) if the network is unavailable, so the TUI still works
    offline after the first run. Raises if neither the network nor a cache
    is available.
    """
    try:
        request = urllib.request.Request(
            THEME_SCHEMA_URL, headers={"User-Agent": "vonage-config-tui"}
        )
        with urllib.request.urlopen(
            request, timeout=_SCHEMA_FETCH_TIMEOUT_SECONDS
        ) as response:
            raw = response.read().decode("utf-8")
        schema = json.loads(raw)
        try:
            _THEME_SCHEMA_CACHE_FILE.parent.mkdir(parents=True, exist_ok=True)
            _THEME_SCHEMA_CACHE_FILE.write_text(raw, encoding="utf-8")
        except OSError:
            pass  # Caching is best-effort; a failed write shouldn't break validation.
        return schema
    except (urllib.error.URLError, TimeoutError, ValueError) as fetch_error:
        if _THEME_SCHEMA_CACHE_FILE.exists():
            return json.loads(_THEME_SCHEMA_CACHE_FILE.read_text(encoding="utf-8"))
        raise RuntimeError(
            f"Could not fetch theme schema from {THEME_SCHEMA_URL} and no "
            "cached copy is available. Connect to the network and retry."
        ) from fetch_error


APP_CONFIG_SCHEMA: dict[str, Any] = _load_schema("app-config.schema.json")
THEME_SCHEMA: dict[str, Any] = _fetch_theme_schema()

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
