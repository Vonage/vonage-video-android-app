"""Form widget: fields on the left, details panel on the right."""

from __future__ import annotations

from typing import Any, Callable

from rich.console import Group, RenderableType
from rich.panel import Panel
from rich.table import Table
from rich.text import Text
from textual import events
from textual.reactive import reactive
from textual.widget import Widget

from . import palette as pal
from .schema_to_fields import FormField

SaveCallback = Callable[[dict[str, Any]], None]
CancelCallback = Callable[[], None]


class Form(Widget, can_focus=True):
    """Interactive form driven by a list of ``FormField``.

    - ↑/k, ↓/j    move cursor (skipping section headers)
    - ⏎, space    toggle bool / cycle enum / start editing string,int,color
    - s, ctrl+s   save (invokes ``on_save`` with the current values dict)
    - esc, q      cancel (invokes ``on_cancel``)

    While editing a text-like field, keys type into an inline buffer; ⏎ commits,
    esc cancels.
    """

    DEFAULT_CSS = """
    Form {
        height: 1fr;
        width: 1fr;
    }
    """

    cursor: reactive[int] = reactive(0)
    editing: reactive[bool] = reactive(False)
    redraw_tick: reactive[int] = reactive(0)
    scroll_offset: reactive[int] = reactive(0)

    def __init__(
        self,
        fields: list[FormField],
        on_save: SaveCallback,
        on_cancel: CancelCallback,
    ) -> None:
        super().__init__()
        self.fields = fields
        self.values: dict[str, Any] = {
            f.key: f.value for f in fields if f.type != "section"
        }
        self.on_save = on_save
        self.on_cancel = on_cancel
        self.edit_buffer: str = ""
        first = next((i for i, f in enumerate(fields) if f.type != "section"), 0)
        # Set before mount so no watcher fires yet.
        self.set_reactive(Form.cursor, first)
        self.set_reactive(Form.scroll_offset, 0)

    # ------------------------------------------------------------------ mount

    def on_mount(self) -> None:
        self.focus()

    def render(self) -> RenderableType:
        table = Table.grid(padding=(0, 1), expand=True)
        table.add_column(ratio=7)
        table.add_column(ratio=3)
        table.add_row(self._render_fields(), self._render_details())
        return table

    # ------------------------------------------------------------------ state

    def watch_cursor(self, _old: int, _new: int) -> None:
        self.refresh()

    def watch_editing(self, _old: bool, _new: bool) -> None:
        self.refresh()

    def watch_redraw_tick(self, _old: int, _new: int) -> None:
        self.refresh()

    def watch_scroll_offset(self, _old: int, _new: int) -> None:
        self.refresh()

    def _visible_height(self) -> int:
        """Usable lines for the fields column (total height minus the hint line + blank)."""
        return max(1, self.size.height - 2)

    def _next_editable(self, from_idx: int, direction: int) -> int:
        i = from_idx + direction
        while 0 <= i < len(self.fields):
            if self.fields[i].type != "section":
                return i
            i += direction
        return from_idx

    # ------------------------------------------------------------------ keys

    def on_key(self, event: events.Key) -> None:
        if self.editing:
            self._handle_edit_key(event)
            return
        key = event.key
        if key in ("escape", "q"):
            event.stop()
            self.on_cancel()
            return
        if key in ("up", "k"):
            event.stop()
            self.cursor = self._next_editable(self.cursor, -1)
            return
        if key in ("down", "j"):
            event.stop()
            self.cursor = self._next_editable(self.cursor, 1)
            return
        if key in ("s", "ctrl+s"):
            event.stop()
            self.on_save(dict(self.values))
            return
        if key in ("enter", "space"):
            event.stop()
            field = self.fields[self.cursor]
            if field.type == "boolean":
                self.values[field.key] = not bool(self.values.get(field.key, False))
                self.redraw_tick += 1
            elif field.type == "enum":
                vals = field.enum_values or []
                if vals:
                    current = str(self.values.get(field.key, vals[0]))
                    idx = vals.index(current) if current in vals else -1
                    self.values[field.key] = vals[(idx + 1) % len(vals)]
                    self.redraw_tick += 1
            else:
                self.edit_buffer = str(self.values.get(field.key, ""))
                self.editing = True

    def _handle_edit_key(self, event: events.Key) -> None:
        key = event.key
        if key == "escape":
            event.stop()
            self.editing = False
            return
        if key == "enter":
            event.stop()
            field = self.fields[self.cursor]
            new_value: Any = self.edit_buffer
            if field.type == "integer":
                try:
                    new_value = int(self.edit_buffer.strip())
                except ValueError:
                    # Refuse the commit; stay in edit mode.
                    return
            self.values[field.key] = new_value
            self.editing = False
            self.redraw_tick += 1
            return
        if key == "backspace":
            event.stop()
            self.edit_buffer = self.edit_buffer[:-1]
            self.redraw_tick += 1
            return
        # Regular character
        ch = event.character
        if ch and len(ch) == 1 and ch.isprintable():
            event.stop()
            self.edit_buffer += ch
            self.redraw_tick += 1

    # ------------------------------------------------------------------ views

    def _render_fields(self) -> RenderableType:
        rows: list[Text] = []
        # Map from field index → row index (so scroll-to-cursor works correctly).
        field_to_row: dict[int, int] = {}

        for i, field in enumerate(self.fields):
            if field.type == "section":
                if i > 0:
                    rows.append(Text(""))
                header = Text()
                header.append("▸ ", style=f"{pal.ACCENT} bold")
                header.append(field.label, style=f"{pal.TEXT} bold dim")
                rows.append(header)
                continue

            field_to_row[i] = len(rows)
            active = i == self.cursor
            line = Text()
            line.append("❯ " if active else "  ", style=pal.PRIMARY if active else pal.MUTED)

            label = field.label
            if len(label) > 26:
                label = label[:25] + "…"
            line.append(f"{label:<28}", style=pal.TEXT if active else pal.SECONDARY)
            line.append(" ")

            if active and self.editing and field.type != "section":
                line.append("▎", style=pal.ACCENT)
                line.append(self.edit_buffer, style=pal.TEXT)
                line.append("█", style=f"{pal.ACCENT} blink")
            else:
                line.append(self._render_value(field, self.values.get(field.key), active))
            rows.append(line)

        # Scroll to keep the cursor row visible (using actual row index, not field index).
        visible = self._visible_height()
        cursor_row = field_to_row.get(self.cursor, 0)
        if cursor_row < self.scroll_offset:
            self.scroll_offset = cursor_row
        elif cursor_row >= self.scroll_offset + visible:
            self.scroll_offset = cursor_row - visible + 1

        total = len(rows)
        offset = max(0, min(self.scroll_offset, max(0, total - visible)))
        visible_rows = rows[offset: offset + visible]

        # Scroll position indicator appended to the hint line.
        pos_hint = f" ({offset + 1}-{min(offset + visible, total)}/{total})" if total > visible else ""
        visible_rows.append(Text(""))
        visible_rows.append(Text(
            f"↑↓ navigate  ⏎/space toggle  s/ctrl+s save  q/esc back{pos_hint}",
            style=pal.MUTED,
        ))
        return Group(*visible_rows)

    def _render_value(self, field: FormField, value: Any, active: bool) -> Text:
        t = Text()
        if field.type == "boolean":
            on = bool(value)
            t.append("● enabled" if on else "○ disabled",
                     style=pal.SUCCESS if on else pal.ERROR)
            return t
        if field.type == "enum":
            t.append(str(value), style=pal.ACCENT if active else pal.MUTED)
            return t
        if field.type == "color":
            hex_val = str(value)
            try:
                t.append("██", style=hex_val)
            except Exception:
                t.append("██")
            t.append(f" {hex_val}", style=pal.TEXT if active else pal.MUTED)
            return t
        t.append(str(value), style=pal.TEXT if active else pal.MUTED)
        return t

    def _render_details(self) -> RenderableType:
        parts: list[RenderableType] = []
        parts.append(Text("◇ Details", style=f"{pal.ACCENT} bold"))
        parts.append(Text(""))

        current = self.fields[self.cursor] if 0 <= self.cursor < len(self.fields) else None
        if current is not None:
            parts.append(Text(current.label, style=f"{pal.TEXT} bold"))
            parts.append(Text(""))
            parts.append(Text(
                current.description or "No description.",
                style=pal.SECONDARY,
            ))

            if current.type == "enum" and current.enum_values:
                parts.append(Text(""))
                parts.append(Text("Options:", style=f"{pal.ACCENT} dim"))
                current_val = str(self.values.get(current.key, ""))
                for v in current.enum_values:
                    selected = v == current_val
                    marker = " ● " if selected else " ○ "
                    parts.append(Text(
                        f"{marker}{v}",
                        style=pal.TEXT if selected else pal.MUTED,
                    ))
            elif current.type == "integer":
                parts.append(Text(""))
                mn = current.min if current.min is not None else "–∞"
                mx = current.max if current.max is not None else "∞"
                parts.append(Text(f"Range: {mn} – {mx}", style=pal.MUTED))
            elif current.type == "color":
                parts.append(Text(""))
                parts.append(Text("Format: #RRGGBB", style=pal.MUTED))
                val = str(self.values.get(current.key, "#000000"))
                try:
                    parts.append(Text("████████", style=val))
                except Exception:
                    parts.append(Text("████████"))
            elif current.type == "boolean":
                parts.append(Text(""))
                parts.append(Text("⏎/space to toggle", style=pal.MUTED))

            parts.append(Text(""))
            parts.append(Text(
                f"{self.cursor + 1}/{len(self.fields)}",
                style=pal.MUTED,
            ))

        return Panel(
            Group(*parts),
            border_style=pal.BORDER,
            padding=(0, 1),
        )
