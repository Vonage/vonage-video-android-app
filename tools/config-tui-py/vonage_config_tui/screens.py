"""Textual screens: main menu, app-config editor, theme editor.

Mirrors src/index.tsx + src/screens/{app-config,theme}.tsx.
"""

from __future__ import annotations

import time
from copy import deepcopy
from typing import Any, Optional

from rich.console import Group, RenderableType
from rich.text import Text
from textual import events
from textual.app import ComposeResult
from textual.binding import Binding
from textual.reactive import reactive
from textual.screen import Screen
from textual.widgets import Static

from . import palette as pal
from .file_writer import save_app_config, save_theme_config
from .form import Form
from .gradle import GradleResult, run_gradle_task
from .schema_to_fields import FormField, fields_to_data, schema_to_fields
from .validator import (
    APP_CONFIG_SCHEMA,
    THEME_SCHEMA,
    load_app_config,
    load_theme_config,
    validate_app_config_data,
    validate_theme_data,
)

SPINNER_FRAMES = ["⠋", "⠙", "⠹", "⠸", "⠼", "⠴", "⠦", "⠧", "⠇", "⠏"]


def _header(title: str) -> Text:
    bar_len = min(60, len(title) + 10)
    return Text.assemble(
        ("◆ ", f"{pal.PRIMARY} bold"),
        (title, f"{pal.TEXT} bold"),
        "\n",
        ("─" * bar_len, pal.BORDER),
    )


def _status_line(msg: str, kind: str) -> Text:
    color = {
        "info": pal.MUTED,
        "success": pal.SUCCESS,
        "error": pal.ERROR,
        "warning": pal.WARNING,
    }.get(kind, pal.MUTED)
    icon = {"info": "●", "success": "✓", "error": "✗", "warning": "⚠"}.get(kind, "●")
    return Text(f"{icon} {msg}", style=color)


# =========================================================================== #
# Main menu
# =========================================================================== #

_MENU_ITEMS: list[tuple[str, str, str]] = [
    ("app-config", "App Config", "Feature toggles, base URL, settings"),
    ("theme", "Theme", "Colors, border radius, typography"),
    ("launch", "Launch App", "Build & install debug APK on device"),
]


class MainMenuScreen(Screen):
    BINDINGS = [
        Binding("q", "quit_app", "quit", show=False),
        Binding("escape", "quit_app", "quit", show=False),
        Binding("up,k", "cursor_up", show=False),
        Binding("down,j", "cursor_down", show=False),
        Binding("enter", "select", show=False),
    ]

    cursor: reactive[int] = reactive(0)
    launching: reactive[bool] = reactive(False)
    current_task: reactive[str] = reactive("")
    spinner_frame: reactive[int] = reactive(0)
    status_msg: reactive[str] = reactive("")
    status_kind: reactive[str] = reactive("info")

    def __init__(self) -> None:
        super().__init__()
        self._start_time: float = 0.0
        self.app_valid: bool = True
        self.theme_valid: bool = True

    def compose(self) -> ComposeResult:
        yield Static("", id="content")

    def on_mount(self) -> None:
        self._refresh_validation()
        self._refresh_view()
        self.set_interval(0.08, self._advance_spinner)

    def on_screen_resume(self) -> None:
        # Re-validate when returning from a pushed screen (config may have changed).
        self._refresh_validation()
        self._refresh_view()

    def _advance_spinner(self) -> None:
        if self.launching:
            self.spinner_frame = (self.spinner_frame + 1) % len(SPINNER_FRAMES)
            self._refresh_view()

    def _refresh_validation(self) -> None:
        try:
            _, v = load_app_config()
            self.app_valid = v.valid
        except Exception:
            self.app_valid = False
        try:
            _, v = load_theme_config()
            self.theme_valid = v.valid
        except Exception:
            self.theme_valid = False

    # --- reactive watchers -------------------------------------------------

    def watch_cursor(self, _o: int, _n: int) -> None: self._refresh_view()
    def watch_launching(self, _o: bool, _n: bool) -> None: self._refresh_view()
    def watch_current_task(self, _o: str, _n: str) -> None: self._refresh_view()
    def watch_status_msg(self, _o: str, _n: str) -> None: self._refresh_view()

    # --- render ------------------------------------------------------------

    def _refresh_view(self) -> None:
        parts: list[RenderableType] = []

        title = Text()
        title.append("  ◆  ", style=f"{pal.PRIMARY} bold")
        title.append("Vonage Video Config", style=f"{pal.PRIMARY} bold")
        parts.append(title)
        parts.append(Text(
            "     Edit config & theme, validate, and regenerate",
            style=pal.MUTED,
        ))
        parts.append(Text(""))

        for i, (key, label, desc) in enumerate(_MENU_ITEMS):
            active = i == self.cursor
            row = Text()
            row.append("❯ " if active else "  ", style=pal.PRIMARY if active else pal.MUTED)
            row.append(label, style=f"{pal.TEXT} bold" if active else pal.SECONDARY)
            row.append(f" — {desc}", style=pal.MUTED)
            if key == "app-config":
                row.append("  " + ("✓" if self.app_valid else "✗"),
                           style=pal.SUCCESS if self.app_valid else pal.ERROR)
            elif key == "theme":
                row.append("  " + ("✓" if self.theme_valid else "✗"),
                           style=pal.SUCCESS if self.theme_valid else pal.ERROR)
            elif key == "launch":
                row.append("  ▶", style=pal.ACCENT)
            parts.append(row)

        if self.launching:
            parts.append(Text(""))
            elapsed = int(time.time() - self._start_time)
            elapsed_str = f"{elapsed // 60}m {elapsed % 60}s" if elapsed >= 60 else f"{elapsed}s"
            head = Text()
            head.append(f"  {SPINNER_FRAMES[self.spinner_frame]} ", style=pal.ACCENT)
            head.append("Building... ", style=f"{pal.TEXT} bold")
            head.append(elapsed_str, style=pal.MUTED)
            parts.append(head)
            parts.append(Text(
                "     " + (self.current_task or "Starting Gradle..."),
                style=pal.SECONDARY,
                overflow="ellipsis",
                no_wrap=True,
            ))

        if self.status_msg:
            parts.append(Text(""))
            parts.append(_status_line(self.status_msg, self.status_kind))

        parts.append(Text(""))
        hint = Text()
        hint.append("↑↓", style=f"{pal.PRIMARY} bold")
        hint.append(" navigate  ", style=pal.MUTED)
        hint.append("⏎", style=f"{pal.PRIMARY} bold")
        hint.append(" select  ", style=pal.MUTED)
        hint.append("q", style=f"{pal.PRIMARY} bold")
        hint.append(" quit", style=pal.MUTED)
        parts.append(hint)

        self.query_one("#content", Static).update(Group(*parts))

    # --- actions -----------------------------------------------------------

    def action_quit_app(self) -> None:
        if not self.launching:
            self.app.exit()

    def action_cursor_up(self) -> None:
        if self.launching:
            return
        self.cursor = max(0, self.cursor - 1)

    def action_cursor_down(self) -> None:
        if self.launching:
            return
        self.cursor = min(len(_MENU_ITEMS) - 1, self.cursor + 1)

    def action_select(self) -> None:
        if self.launching:
            return
        key = _MENU_ITEMS[self.cursor][0]
        if key == "launch":
            self._start_time = time.time()
            self.status_msg = ""
            self.current_task = ""
            self.launching = True
            self.run_worker(self._install_debug, thread=True, exclusive=True)
        elif key == "app-config":
            self.app.push_screen(AppConfigScreen())
        elif key == "theme":
            self.app.push_screen(ThemeScreen())

    def _install_debug(self) -> None:
        def on_progress(line: str) -> None:
            # Same filter as the TS version: task/configure lines, BUILD lines, ':' prefixed, install.
            if (
                line.startswith("> Task")
                or line.startswith("> Configure")
                or "BUILD" in line
                or line.startswith(":")
                or "install" in line
            ):
                self.app.call_from_thread(self._set_current_task, line)

        result = run_gradle_task("installDebug", on_progress)
        self.app.call_from_thread(self._install_finished, result)

    def _set_current_task(self, line: str) -> None:
        self.current_task = line

    def _install_finished(self, result: GradleResult) -> None:
        if result.success:
            self.status_kind = "success"
            self.status_msg = f"App installed successfully! ({result.duration_ms / 1000:.1f}s)"
        else:
            self.status_kind = "error"
            self.status_msg = f"Build failed: {result.output[-150:]}"
        self.launching = False
        self.current_task = ""
        self._refresh_validation()
        self._refresh_view()


# =========================================================================== #
# App Config
# =========================================================================== #


class AppConfigScreen(Screen):
    BINDINGS = [Binding("escape", "back", "back", show=False)]

    status_msg: reactive[str] = reactive("")
    status_kind: reactive[str] = reactive("info")
    running: reactive[bool] = reactive(False)
    spinner_frame: reactive[int] = reactive(0)

    def __init__(self) -> None:
        super().__init__()
        self._form: Optional[Form] = None
        self._data: dict[str, Any] = {}
        self._validation_error: Optional[str] = None

    def compose(self) -> ComposeResult:
        yield Static("", id="ac-header")
        yield Static("", id="ac-warning")
        # Form is created after we load the data in on_mount.
        yield Static("", id="ac-form-slot")
        yield Static("", id="ac-status")

    def on_mount(self) -> None:
        self._reload()
        self.set_interval(0.08, self._advance_spinner)

    def _advance_spinner(self) -> None:
        if self.running:
            self.spinner_frame = (self.spinner_frame + 1) % len(SPINNER_FRAMES)
            self._render_running()

    def _reload(self) -> None:
        data, validation = load_app_config()
        self._data = data
        self._validation_error = None if validation.valid else validation.errors[0]

        self.query_one("#ac-header", Static).update(_header("App Configuration"))
        warn = self.query_one("#ac-warning", Static)
        if self._validation_error:
            warn.update(_status_line(f"Current file has issues: {self._validation_error}", "warning"))
        else:
            warn.update("")

        fields = schema_to_fields(APP_CONFIG_SCHEMA, data)
        if self._form is not None:
            self._form.remove()
        self._form = Form(fields, self._on_save, self._on_cancel)
        slot = self.query_one("#ac-form-slot", Static)
        slot.update("")
        self.mount(self._form, after=slot)
        self._form.focus()

    def _render_status(self) -> None:
        widget = self.query_one("#ac-status", Static)
        if self.status_msg:
            widget.update(_status_line(self.status_msg, self.status_kind))
        else:
            widget.update("")

    def _render_running(self) -> None:
        line = Text()
        line.append(f"{SPINNER_FRAMES[self.spinner_frame]} ", style=pal.ACCENT)
        line.append("Running Gradle task...", style=pal.MUTED)
        self.query_one("#ac-status", Static).update(line)

    def watch_status_msg(self, _o: str, _n: str) -> None:
        if not self.running:
            self._render_status()

    def watch_running(self, _o: bool, _n: bool) -> None:
        if self.running:
            self._render_running()
        else:
            self._render_status()

    # --- form callbacks ----------------------------------------------------

    def _on_save(self, values: dict[str, Any]) -> None:
        config = fields_to_data(values)
        result = validate_app_config_data(config)
        if not result.valid:
            self.status_kind = "error"
            self.status_msg = f"Validation failed: {result.errors[0]}"
            return

        save_app_config(config)
        self.status_kind = "info"
        self.status_msg = "Saved app-config.json. Running clean generateVonageConfig..."
        self.running = True
        self.run_worker(self._run_generate, thread=True, exclusive=True)

    def _on_cancel(self) -> None:
        self.app.pop_screen()

    def _run_generate(self) -> None:
        result = run_gradle_task("generateVonageConfig")
        self.app.call_from_thread(self._generate_finished, result)

    def _generate_finished(self, result: GradleResult) -> None:
        if result.success:
            self.status_kind = "success"
            self.status_msg = "Config generated successfully!"
        else:
            self.status_kind = "error"
            self.status_msg = f"Gradle failed: {result.output[:120]}"
        self.running = False
        # Reload from disk (validation status may have changed).
        self._reload()

    def action_back(self) -> None:
        if not self.running:
            self.app.pop_screen()


# =========================================================================== #
# Theme
# =========================================================================== #

_THEME_SECTIONS: list[tuple[str, str, list[str]]] = [
    ("colors-light", "Light Colors", ["themes", "vonage", "colors", "light"]),
    ("colors-dark", "Dark Colors", ["themes", "vonage", "colors", "dark"]),
    ("border-radius", "Border Radius", ["themes", "vonage", "borderRadius"]),
    ("typography", "Typography", ["themes", "vonage", "typography"]),
]

_SECTION_DEF_REF: dict[str, str] = {
    "colors-light": "colorScheme",
    "colors-dark": "colorScheme",
    "border-radius": "borderRadius",
    "typography": "typography",
}


def _resolve_def(schema: dict[str, Any], ref_path: str) -> dict[str, Any]:
    name = ref_path.replace("#/$defs/", "")
    return schema.get("$defs", {}).get(name, {})


def _get_nested(obj: Any, path: list[str]) -> Any:
    current = obj
    for key in path:
        if not isinstance(current, dict):
            return {}
        current = current.get(key)
    return current if current is not None else {}


def _set_nested(obj: dict[str, Any], path: list[str], value: Any) -> dict[str, Any]:
    clone = deepcopy(obj)
    target = clone
    for key in path[:-1]:
        target = target[key]  # type: ignore[assignment]
    target[path[-1]] = value
    return clone


class ThemeScreen(Screen):
    BINDINGS = [
        Binding("q", "back", "back", show=False),
        Binding("escape", "back", "back", show=False),
        Binding("up,k", "cursor_up", show=False),
        Binding("down,j", "cursor_down", show=False),
        Binding("enter", "select", show=False),
    ]

    section: reactive[str] = reactive("menu")  # "menu" or one of _THEME_SECTIONS keys
    cursor: reactive[int] = reactive(0)
    status_msg: reactive[str] = reactive("")
    status_kind: reactive[str] = reactive("info")
    running: reactive[bool] = reactive(False)
    spinner_frame: reactive[int] = reactive(0)

    def __init__(self) -> None:
        super().__init__()
        self._data: dict[str, Any] = {}
        self._validation_error: Optional[str] = None
        self._form: Optional[Form] = None
        self._current_section_path: list[str] = []

    def compose(self) -> ComposeResult:
        yield Static("", id="th-content")
        yield Static("", id="th-form-slot")
        yield Static("", id="th-status")

    def on_mount(self) -> None:
        self._reload()
        self.set_interval(0.08, self._advance_spinner)

    def _advance_spinner(self) -> None:
        if self.running:
            self.spinner_frame = (self.spinner_frame + 1) % len(SPINNER_FRAMES)
            self._render_running()

    def _reload(self) -> None:
        data, validation = load_theme_config()
        self._data = data
        self._validation_error = None if validation.valid else validation.errors[0]
        self._refresh_view()

    # --- watchers ----------------------------------------------------------

    def watch_section(self, _o: str, _n: str) -> None: self._refresh_view()
    def watch_cursor(self, _o: int, _n: int) -> None:
        if self.section == "menu":
            self._refresh_view()
    def watch_status_msg(self, _o: str, _n: str) -> None:
        if not self.running:
            self._render_status()
    def watch_running(self, _o: bool, _n: bool) -> None:
        if self.running:
            self._render_running()
        else:
            self._render_status()

    # --- render ------------------------------------------------------------

    def _clear_form(self) -> None:
        if self._form is not None:
            try:
                self._form.remove()
            except Exception:
                pass
            self._form = None

    def _refresh_view(self) -> None:
        if self.running:
            # Keep header visible; form area shows spinner via status.
            self.query_one("#th-content", Static).update(_header("Theme Editor"))
            self._render_running()
            return

        if self.section == "menu":
            self._clear_form()
            self._render_menu()
        else:
            self._render_section_form()

    def _render_menu(self) -> None:
        parts: list[RenderableType] = [_header("Theme Editor")]
        if self._validation_error:
            parts.append(_status_line(
                f"Current theme.json has validation issues: {self._validation_error}",
                "warning",
            ))
        parts.append(Text(""))
        parts.append(Text.assemble(
            ("▸ ", f"{pal.ACCENT} bold"),
            ("Select a section to edit", f"{pal.TEXT} dim"),
        ))
        parts.append(Text(""))

        items = [(key, label) for key, label, _ in _THEME_SECTIONS]
        for i, (_key, label) in enumerate(items):
            active = i == self.cursor
            row = Text()
            row.append("❯ " if active else "  ", style=pal.PRIMARY if active else pal.MUTED)
            row.append(label, style=pal.TEXT if active else pal.SECONDARY)
            parts.append(row)

        parts.append(Text(""))
        parts.append(Text("↑↓ navigate  ⏎ select  q/esc back", style=pal.MUTED))
        self.query_one("#th-content", Static).update(Group(*parts))
        self._render_status()

    def _render_section_form(self) -> None:
        section_key = self.section
        label = next((lbl for k, lbl, _ in _THEME_SECTIONS if k == section_key), section_key)
        self.query_one("#th-content", Static).update(_header(f"Theme: {label}"))

        # Build sub-schema
        ref_name = _SECTION_DEF_REF[section_key]
        sub = _resolve_def(THEME_SCHEMA, f"#/$defs/{ref_name}")
        # Inject $defs so nested $refs still resolve.
        sub_schema = dict(sub)
        sub_schema["$defs"] = THEME_SCHEMA.get("$defs", {})

        data_path = next(p for k, _, p in _THEME_SECTIONS if k == section_key)
        self._current_section_path = data_path
        sub_data = _get_nested(self._data, data_path)
        fields = schema_to_fields(sub_schema, sub_data)

        self._clear_form()
        self._form = Form(fields, self._on_save, self._on_cancel_section)
        slot = self.query_one("#th-form-slot", Static)
        slot.update("")
        self.mount(self._form, after=slot)
        self._form.focus()
        self._render_status()

    def _render_status(self) -> None:
        widget = self.query_one("#th-status", Static)
        if self.status_msg:
            widget.update(_status_line(self.status_msg, self.status_kind))
        else:
            widget.update("")

    def _render_running(self) -> None:
        line = Text()
        line.append(f"{SPINNER_FRAMES[self.spinner_frame]} ", style=pal.ACCENT)
        line.append("Running Gradle generateTheme...", style=pal.MUTED)
        self.query_one("#th-status", Static).update(line)

    # --- actions -----------------------------------------------------------

    def action_back(self) -> None:
        if self.running:
            return
        # Only trigger at menu view; when a Form is active it has focus and eats esc/q.
        if self.section == "menu":
            self.app.pop_screen()

    def action_cursor_up(self) -> None:
        if self.section != "menu" or self.running:
            return
        self.cursor = max(0, self.cursor - 1)

    def action_cursor_down(self) -> None:
        if self.section != "menu" or self.running:
            return
        self.cursor = min(len(_THEME_SECTIONS) - 1, self.cursor + 1)

    def action_select(self) -> None:
        if self.section != "menu" or self.running:
            return
        key, _, _ = _THEME_SECTIONS[self.cursor]
        self.section = key  # type: ignore[assignment]

    # --- form callbacks ----------------------------------------------------

    def _on_cancel_section(self) -> None:
        self.section = "menu"

    def _on_save(self, values: dict[str, Any]) -> None:
        rebuilt = fields_to_data(values)
        updated = _set_nested(self._data, self._current_section_path, rebuilt)

        result = validate_theme_data(updated)
        if not result.valid:
            self.status_kind = "error"
            self.status_msg = f"Validation failed: {result.errors[0]}"
            return

        save_theme_config(updated)
        self._data = updated
        self.status_kind = "info"
        self.status_msg = "Saved theme.json. Running clean generateTheme..."
        self.running = True
        self.run_worker(self._run_generate, thread=True, exclusive=True)

    def _run_generate(self) -> None:
        result = run_gradle_task("generateTheme")
        self.app.call_from_thread(self._generate_finished, result)

    def _generate_finished(self, result: GradleResult) -> None:
        if result.success:
            self.status_kind = "success"
            self.status_msg = "Theme generated successfully!"
        else:
            self.status_kind = "error"
            self.status_msg = f"Gradle failed: {result.output[:120]}"
        self.running = False
        self.section = "menu"
        self._reload()
