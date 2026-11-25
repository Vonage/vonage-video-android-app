#!/usr/bin/env bash
# Join/leave memory profiling on a connected device or emulator.
#
# Records memory counters for the whole run, takes a Java heap dump on Landing (baseline) and one on
# Goodbye after CYCLES calls, samples `dumpsys meminfo` after every call, then writes report.md with
# advisory warnings (memory_report.py). Needs a debuggable build (Java heap dumps), Maestro, adb,
# python3 and curl. Joins a real room on the configured backend.
#
# Usage: tools/profiling/memory-cycles.sh
# Env:   APP_ID (com.vonage.android.debug)  ROOM_NAME (profiling-<timestamp>)  CYCLES (5)
#        CALL_MS (20000)  OUT (build/profiling)
set -euo pipefail

APP_ID="${APP_ID:-com.vonage.android.debug}"
ROOM_NAME="${ROOM_NAME:-profiling-$(date +%s)}"
CYCLES="${CYCLES:-5}"
CALL_MS="${CALL_MS:-20000}"
OUT="${OUT:-build/profiling}"
PERFETTO_VERSION="v58.3"

HERE="$(cd "$(dirname "$0")" && pwd)"
mkdir -p "$OUT/bin"
OUT="$(cd "$OUT" && pwd)"

for tool in record_android_trace java_heap_dump; do
  if [[ ! -x "$OUT/bin/$tool" ]]; then
    curl -sfSL "https://raw.githubusercontent.com/google/perfetto/$PERFETTO_VERSION/tools/$tool" -o "$OUT/bin/$tool"
    chmod +x "$OUT/bin/$tool"
  fi
done

heap_dump() { "$OUT/bin/java_heap_dump" -n "$APP_ID" -o "$OUT/$1.perfetto-trace" > "$OUT/$1.log" 2>&1; }
meminfo() { adb shell dumpsys meminfo "$APP_ID" > "$OUT/meminfo_$1.txt"; }
maestro_flow() {
  maestro test --env APP_ID="$APP_ID" --env ROOM_NAME="$ROOM_NAME" --env CALL_MS="$CALL_MS" "$@"
}

echo "Room: $ROOM_NAME, cycles: $CYCLES, output: $OUT"
for permission in CAMERA RECORD_AUDIO BLUETOOTH_CONNECT POST_NOTIFICATIONS; do
  adb shell pm grant "$APP_ID" "android.permission.$permission" 2>/dev/null || true
done

# Start MainActivity explicitly: debug builds also have a LeakCanary launcher activity.
adb shell am force-stop "$APP_ID"
adb shell am start -W -n "$APP_ID/com.vonage.android.MainActivity" > /dev/null
sleep 5

sed "s/APP_ID/$APP_ID/" "$HERE/memory_counters.pftxt" > "$OUT/memory_counters.pftxt"
"$OUT/bin/record_android_trace" -n -c "$OUT/memory_counters.pftxt" \
  -o "$OUT/memory_counters.perfetto-trace" > "$OUT/record.log" 2>&1 &
TRACE_PID=$!
stop_trace() { kill -INT "$TRACE_PID" 2>/dev/null && wait "$TRACE_PID" || true; }
trap stop_trace EXIT
sleep 5

heap_dump heap_baseline
meminfo 0

maestro_flow "$HERE/flows/open-waiting-room.yaml"
for cycle in $(seq 1 "$CYCLES"); do
  if (( cycle == CYCLES )); then next=stay; elif (( cycle % 2 )); then next=back; else next=reenter; fi
  echo "Cycle $cycle/$CYCLES (then $next)"
  maestro_flow --env NEXT="$next" "$HERE/flows/call-cycle.yaml"
  sleep 5
  meminfo "$cycle"
done

sleep 10
heap_dump heap_final
stop_trace
trap - EXIT

# The report needs the perfetto package; keep it in a throwaway venv, not the system Python.
python3 -m venv "$OUT/venv"
"$OUT/venv/bin/pip" install -q --index-url https://pypi.org/simple/ perfetto
"$OUT/venv/bin/python" "$HERE/memory_report.py" "$OUT"
rm -rf "$OUT/venv"
