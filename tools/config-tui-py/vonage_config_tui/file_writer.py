"""Writes app-config.json / theme.json back to disk"""

from __future__ import annotations

import json
from typing import Any

from .validator import CONFIG_ROOT


def save_app_config(data: dict[str, Any]) -> None:
    (CONFIG_ROOT / "app-config.json").write_text(
        json.dumps(data, indent=2) + "\n", encoding="utf-8"
    )


def save_theme_config(data: dict[str, Any]) -> None:
    (CONFIG_ROOT / "theme.json").write_text(
        json.dumps(data, indent=2) + "\n", encoding="utf-8"
    )
