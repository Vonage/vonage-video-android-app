#!/bin/bash
# Script para ejecutar tests de Maestro en local

# Uso: ./scripts/run_maestro_tests.sh [--auto-emulator | --avd <nombre>]
# Ejemplos:
#   ./run_maestro_tests.sh                              # Auto-launches first available emulator
#   ./run_maestro_tests.sh --auto-emulator              # Same as above (explicit)
#   ./run_maestro_tests.sh --avd Medium_Phone_API_36.1  # Uses the specified emulator

# Cambiar al directorio del proyecto
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_DIR="$(dirname "$SCRIPT_DIR")"
cd "$PROJECT_DIR" || exit 1

# Output colors
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m'

# ========== INSTALL MAESTRO & DEPENDENCIES ==========
echo -e "${BLUE}🚀 Setting up Maestro and dependencies...${NC}"
source "$SCRIPT_DIR/install_maestro.sh"
install_all || exit 1

ADB_PATH="$ANDROID_HOME/platform-tools/adb"
MAESTRO_PATH="$HOME/.maestro/bin/maestro"

AUTO_LAUNCH_EMULATOR=false
SPECIFIC_AVD=""

# Parsear argumentos
while [[ $# -gt 0 ]]; do
    case $1 in
        --auto-emulator)
            AUTO_LAUNCH_EMULATOR=true
            shift
            ;;
        --avd)
            SPECIFIC_AVD="$2"
            AUTO_LAUNCH_EMULATOR=true
            shift 2
            ;;
        *)
            echo "Usage: $0 [--auto-emulator | --avd <name>]"
            exit 1
            ;;
    esac
done

echo -e "${BLUE}🚀 Starting Maestro tests...${NC}"

# ========== VERIFY DEVICE/EMULATOR ==========
echo -e "${BLUE}📲 Checking for connected device/emulator...${NC}"

# Use 'adb devices' (without -l) so the state column is the last field
HAS_DEVICES=$("$ADB_PATH" devices 2>/dev/null | awk 'NR>1 && $2=="device" {count++} END {print count+0}')

if [ "$HAS_DEVICES" -eq 0 ]; then
    echo -e "${RED}❌ No devices or emulators connected${NC}"
    echo -e ""
    
    # Try to list available emulators
    AVAILABLE_AVDS=$("$ANDROID_HOME/emulator/emulator" -list-avds 2>/dev/null || echo "")
    
    if [ ! -z "$AVAILABLE_AVDS" ]; then
        echo -e "${YELLOW}📱 Available emulators:${NC}"
        echo "$AVAILABLE_AVDS"
        echo -e ""
        
        # Decide whether to auto-launch emulator
        LAUNCH_EMU=false
        SELECTED_AVD=""
        
        if [ "$AUTO_LAUNCH_EMULATOR" = true ]; then
            LAUNCH_EMU=true
            if [ ! -z "$SPECIFIC_AVD" ]; then
                SELECTED_AVD="$SPECIFIC_AVD"
            else
                SELECTED_AVD=$(echo "$AVAILABLE_AVDS" | head -n 1)
            fi
            echo -e "${BLUE}Auto-launching emulator: $SELECTED_AVD${NC}"
        else
            # Auto-launch first available emulator without asking
            LAUNCH_EMU=true
            SELECTED_AVD=$(echo "$AVAILABLE_AVDS" | head -n 1)
            echo -e "${BLUE}Launching emulator: $SELECTED_AVD${NC}"
        fi
        
        if [ "$LAUNCH_EMU" = true ]; then
            if [ ! -z "$SELECTED_AVD" ]; then
                echo -e "${BLUE}🚀 Launching emulator: $SELECTED_AVD${NC}"
                "$ANDROID_HOME/emulator/emulator" -avd "$SELECTED_AVD" -no-snapshot-load > /tmp/emulator.log 2>&1 &
                EMULATOR_PID=$!
                
                echo -e "${YELLOW}⏳ Waiting for emulator to boot (this may take 2-3 minutes)...${NC}"
                sleep 20
                
                # Wait for adb to detect device
                max_attempts=120
                attempt=0
                DEVICE_DETECTED=false
                
                while [ $attempt -lt $max_attempts ]; do
                    DEVICE_COUNT=$("$ADB_PATH" devices 2>/dev/null | awk 'NR>1 && $2=="device" {count++} END {print count+0}')
                    if [ "$DEVICE_COUNT" -gt 0 ]; then
                        DEVICE_DETECTED=true
                        echo -e "${GREEN}✓ Emulator detected by adb${NC}"
                        break
                    fi
                    attempt=$((attempt + 1))
                    echo -n "."
                    sleep 1
                done
                
                if [ "$DEVICE_DETECTED" = false ]; then
                    echo -e ""
                    echo -e "${YELLOW}⚠ Emulator took longer than expected but continuing...${NC}"
                fi
                echo ""
            fi
        else
            echo -e "${YELLOW}Aborting...${NC}"
            exit 1
        fi
    else
        echo -e "${YELLOW}⚠ No emulators available${NC}"
        echo -e ""
        echo -e "${YELLOW}To create an emulator:${NC}"
        echo -e "  $ANDROID_HOME/tools/bin/avdmanager create avd -n \"Pixel_4\" -k \"system-images;android-31;google_apis;arm64-v8a\""
        echo -e ""
        echo -e "${YELLOW}Or connect a physical device with USB debugging enabled${NC}"
        exit 1
    fi
fi

# Wait for device/emulator to be fully ready
echo -e "${BLUE}⏳ Waiting for device to be fully ready...${NC}"
"$ADB_PATH" wait-for-device
sleep 3

# ========== SELECT TARGET DEVICE ==========
DEVICE_SERIAL=$("$ADB_PATH" devices 2>/dev/null | awk 'NR>1 && $2=="device" {print $1; exit}')
if [ -z "$DEVICE_SERIAL" ]; then
    echo -e "${RED}❌ No device available after wait${NC}"
    exit 1
fi
echo -e "${GREEN}✓ Using device: $DEVICE_SERIAL${NC}"
export ANDROID_SERIAL="$DEVICE_SERIAL"

# Show connected device(s)
echo -e "${GREEN}✓ Connected device(s):${NC}"
"$ADB_PATH" devices -l | awk 'NR>1 && $2=="device"'

# ========== BUILD APK ==========
echo -e "${BLUE}🔨 Building debug APK...${NC}"
./gradlew assembleDebug
if [ $? -ne 0 ]; then
    echo -e "${RED}❌ Error building APK${NC}"
    exit 1
fi
echo -e "${GREEN}✓ APK built successfully${NC}"

# ========== GET AND VALIDATE APK ==========
APK_PATH="app/build/outputs/apk/debug/app-debug.apk"

if [ ! -f "$APK_PATH" ]; then
    echo -e "${RED}❌ Error: APK not found at $APK_PATH${NC}"
    exit 1
fi
echo -e "${GREEN}✓ APK found: $APK_PATH${NC}"

# ========== INSTALL APK ==========
echo -e "${BLUE}📱 Installing APK on device...${NC}"
"$ADB_PATH" -s "$DEVICE_SERIAL" install -r "$APK_PATH"
if [ $? -ne 0 ]; then
    echo -e "${RED}❌ Error installing APK${NC}"
    echo -e "${YELLOW}Try: adb -s $DEVICE_SERIAL uninstall com.vonage.android.debug${NC}"
    exit 1
fi
echo -e "${GREEN}✓ APK installed successfully${NC}"

# ========== RUN TESTS ==========
echo -e "${BLUE}🧪 Running Maestro tests...${NC}"

# Check if flows directory exists
if [ ! -d ".maestro/flows" ]; then
    echo -e "${RED}❌ Error: .maestro/flows directory not found${NC}"
    echo -e "${YELLOW}Create the directory and add test flow YAML files${NC}"
    exit 1
fi

# Count flows
FLOW_COUNT=$(find .maestro/flows -name "*.yaml" | wc -l)
if [ "$FLOW_COUNT" -eq 0 ]; then
    echo -e "${YELLOW}⚠ Warning: No test flows found in maestro/flows/${NC}"
    echo -e "${YELLOW}Add .yaml files with your tests${NC}"
    exit 0
fi

echo -e "${BLUE}Found $FLOW_COUNT test(s)${NC}"
"$MAESTRO_PATH" test .maestro/flows/

if [ $? -eq 0 ]; then
    echo -e ""
    echo -e "${GREEN}═══════════════════════════════════════${NC}"
    echo -e "${GREEN}✅ Tests completed successfully${NC}"
    echo -e "${GREEN}═══════════════════════════════════════${NC}"
else
    echo -e ""
    echo -e "${RED}═══════════════════════════════════════${NC}"
    echo -e "${RED}❌ Some tests failed${NC}"
    echo -e "${RED}═══════════════════════════════════════${NC}"
    exit 1
fi
