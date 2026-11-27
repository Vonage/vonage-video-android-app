# Memory profiling

`memory-cycles.sh` checks that finished calls release their memory. It joins and leaves a real
room `CYCLES` times and writes `report.md` with advisory warnings. It runs nightly on an emulator
in `.github/workflows/memory-profiling.yml` and also works against a local device or emulator.

## Run locally

Needs a debuggable build (Java heap dumps), `adb`, [Maestro](https://maestro.mobile.dev)
(`scripts/install_maestro.sh`), `python3` and `curl`. Keep the device unlocked and awake for the
whole run (`adb shell svc power stayon usb`, then `adb shell svc power stayon false`).

```bash
./gradlew :app:installDebug
CYCLES=5 ROOM_NAME=my-profiling-room ./tools/profiling/memory-cycles.sh
open build/profiling/report.md
```

| Env | Default | |
|---|---|---|
| `APP_ID` | `com.vonage.android.debug` | Package to profile |
| `ROOM_NAME` | `profiling-<timestamp>` | Room joined on the configured backend |
| `CYCLES` | `5` | Calls; after each one Goodbye is left alternately by Back and Re-enter |
| `CALL_MS` | `20000` | Time spent in each call |
| `OUT` | `build/profiling` | Output directory |

## What it does

1. Starts `MainActivity` and records `memory_counters.pftxt` (RSS, LMK and OOM events) for the whole run.
2. Takes a Java heap dump on Landing (`heap_baseline`).
3. Runs `flows/open-waiting-room.yaml`, then `flows/call-cycle.yaml` once per cycle, saving `dumpsys meminfo` after each.
4. Takes a Java heap dump on Goodbye (`heap_final`) and runs `memory_report.py`.

## Checks (advisory)

| Check | Budget |
|---|---|
| Call objects (`MeetingRoomViewModel`, `Call`, OpenTok `Session`/`Publisher`, `EglCore`, ...) reachable on Goodbye | 0 |
| Reachable Java heap growth, Landing to Goodbye | 10 MB |
| `TOTAL PSS` growth per cycle (waiting-room samples) | 5 MB |

Budgets live at the top of `memory_report.py`. For any retained call object, the report includes
its dominator chain. Open the `.perfetto-trace` files in https://ui.perfetto.dev for more detail.
