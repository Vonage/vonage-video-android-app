"""Textual App entrypoint"""

from __future__ import annotations

from textual.app import App

from .screens import MainMenuScreen


class ConfigTuiApp(App):
    CSS = """
    Screen {
        background: #0d0d0d;
    }
    """

    def on_mount(self) -> None:
        self.push_screen(MainMenuScreen())


def main() -> None:
    ConfigTuiApp().run()


if __name__ == "__main__":
    main()
