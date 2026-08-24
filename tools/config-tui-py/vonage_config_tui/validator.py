"""JSON Schema validation for config/app-config.json and config/theme.json.

Mirrors src/lib/validator.ts (Ajv 2020-12).
"""

from __future__ import annotations

import json
from dataclasses import dataclass
from pathlib import Path
from typing import Any

from jsonschema import Draft202012Validator

# tools/config-tui-py/vonage_config_tui/validator.py
#   parents[0] = vonage_config_tui/
#   parents[1] = config-tui-py/
#   parents[2] = tools/
#   parents[3] = project root
CONFIG_ROOT: Path = Path(__file__).resolve().parents[3] / "config"


def _load_schema(filename: str) -> dict[str, Any]:
    return json.loads((CONFIG_ROOT / filename).read_text(encoding="utf-8"))


APP_CONFIG_SCHEMA: dict[str, Any] = _load_schema("app-config.schema.json")
THEME_SCHEMA: dict[str, Any] = _load_schema("theme.schema.json")

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
