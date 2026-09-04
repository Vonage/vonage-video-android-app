# vonage-config-tui (Python)

Python port of [`tools/config-tui`](../config-tui). A terminal UI for editing
`config/app-config.json` and `config/theme.json`, validating them against
`config/app-config.schema.json` (local) and the unified theme schema (fetched
from a canonical URL, shared with the iOS and React Vonage Video apps), and
running the associated Gradle tasks.

## Install

Requires Python 3.10+.

```bash
# One-off (pipx recommended)
pipx install ./tools/config-tui-py

# Or with a venv
python3 -m venv .venv
source .venv/bin/activate
pip install -r tools/config-tui-py/requirements.txt
pip install -e tools/config-tui-py
```

## Run

```bash
vonage-config
# or, without install:
python -m vonage_config_tui
```

## Behavior parity with the TypeScript version

- Main menu: **App Config**, **Theme**, **Launch App** (runs `./gradlew installDebug`).
- Live ✓/✗ validation indicators next to App Config and Theme.
- Form-driven editing derived from JSON Schemas — app-config from
  `config/app-config.schema.json`, theme from the schema fetched at runtime:
  - `boolean` — toggle with ⏎ / space
  - `enum` — cycle with ⏎ / space
  - `string` / `integer` / hex-`color` — inline edit; ⏎ commits, esc cancels
- After save: `./gradlew clean generateVonageConfig` (app-config) or
  `./gradlew clean :vonage-video-ui-compose:generateTheme` (theme).
- Right-hand details panel shows description, enum options, integer range,
  or color swatch for the selected field.

## Keys

| Key | Action |
|---|---|
| ↑ / k, ↓ / j | Move cursor |
| ⏎ / space | Toggle bool, cycle enum, or start editing text |
| s or Ctrl+S | Save the form |
| esc / q | Back / quit |
