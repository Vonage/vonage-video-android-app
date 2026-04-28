#!/bin/bash
# Script para ejecutar tests de Maestro en local

# Uso: ./run_maestro_tests.sh [--auto-emulator | --avd <nombre>]
# Ejemplos:
#   ./run_maestro_tests.sh                              # Interactivo
#   ./run_maestro_tests.sh --auto-emulator              # Usa el primer emulador disponible
#   ./run_maestro_tests.sh --avd Medium_Phone_API_36.1  # Usa ese emulador específico

# Cambiar al directorio del proyecto
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_DIR="$(dirname "$SCRIPT_DIR")"
cd "$PROJECT_DIR" || exit 1

# ========== SETUP JAVA AUTOMÁTICO ==========
# Intenta usar Java 17, sino usa la versión disponible
JAVA_17=$(/usr/libexec/java_home -v17 2>/dev/null || echo "")
if [ -n "$JAVA_17" ]; then
    export JAVA_HOME="$JAVA_17"
else
    export JAVA_HOME=$(/usr/libexec/java_home)
fi

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

echo "🚀 Starting Maestro tests..."

# Output colors
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# ========== SHOW JAVA CONFIG ==========
echo -e "${BLUE}☕ Java configured automatically${NC}"
echo -e "${GREEN}✓ JAVA_HOME: $JAVA_HOME${NC}"

# ========== SETUP ANDROID SDK ==========
echo -e "${BLUE}🤖 Configuring Android SDK...${NC}"
if [ -z "$ANDROID_HOME" ]; then
    if [ -d "$HOME/Library/Android/sdk" ]; then
        export ANDROID_HOME="$HOME/Library/Android/sdk"
        echo -e "${GREEN}✓ ANDROID_HOME configured: $ANDROID_HOME${NC}"
    else
        echo -e "${RED}❌ Error: Android SDK not found at $HOME/Library/Android/sdk${NC}"
        echo -e "${YELLOW}Please install Android SDK from https://developer.android.com/studio${NC}"
        exit 1
    fi
fi

# ========== SETUP PATH ==========
export PATH="$ANDROID_HOME/platform-tools:$HOME/.maestro/bin:$PATH"

# ========== VERIFY ADB ==========
echo -e "${BLUE}📱 Verifying adb...${NC}"
ADB_PATH="/Users/jsanmartin/Library/Android/sdk/platform-tools/adb"
if [ ! -f "$ADB_PATH" ]; then
    echo -e "${RED}❌ Error: adb not found at $ADB_PATH${NC}"
    exit 1
fi
echo -e "${GREEN}✓ adb found: $ADB_PATH${NC}"

# ========== VERIFY MAESTRO ==========
echo -e "${BLUE}🧪 Verifying Maestro CLI...${NC}"
MAESTRO_PATH="/Users/jsanmartin/.maestro/bin/maestro"
if [ ! -f "$MAESTRO_PATH" ]; then
    echo -e "${YELLOW}📦 Maestro is not installed. Installing...${NC}"
    curl -fsSL "https://get.maestro.mobile.dev" | bash
    if [ $? -eq 0 ]; then
        echo -e "${GREEN}✓ Maestro installed successfully${NC}"
    else
        echo -e "${RED}❌ Error installing Maestro${NC}"
        exit 1
    fi
else
    echo -e "${GREEN}✓ Maestro found: $MAESTRO_PATH${NC}"
fi

# ========== VERIFY DEVICE/EMULATOR ==========
echo -e "${BLUE}📲 Checking for connected device/emulator...${NC}"

# Get device list properly
DEVICE_LIST=$(/Users/jsanmartin/Library/Android/sdk/platform-tools/adb devices -l 2>&1)
DEVICE_COUNT=$(echo "$DEVICE_LIST" | grep -c "device" | grep -v "List of attached devices")

# Check if there are devices (not offline)
HAS_DEVICES=$(echo "$DEVICE_LIST" | grep -E "device|emulator" | grep -v "List of attached" | grep "device$" | wc -l)

if [ "$HAS_DEVICES" -eq 0 ]; then
    echo -e "${RED}❌ No devices or emulators connected${NC}"
    echo -e ""
    
    # Try to list available emulators
    AVAILABLE_AVDS=$(/Users/jsanmartin/Library/Android/sdk/emulator/emulator -list-avds 2>/dev/null || echo "")
    
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
            # Ask if want to launch an emulator
            read -p "Do you want to launch an emulator? (y/n): " -n 1 -r
            echo
            if [[ $REPLY =~ ^[Yy]$ ]]; then
                LAUNCH_EMU=true
                # Use first available emulator as default
                DEFAULT_AVD=$(echo "$AVAILABLE_AVDS" | head -n 1)
                read -p "Enter emulator name (default: $DEFAULT_AVD): " AVD_NAME
                
                # If empty, use default
                if [ -z "$AVD_NAME" ]; then
                    SELECTED_AVD="$DEFAULT_AVD"
                else
                    SELECTED_AVD="$AVD_NAME"
                fi
            fi
        fi
        
        if [ "$LAUNCH_EMU" = true ]; then
            if [ ! -z "$SELECTED_AVD" ]; then
                echo -e "${BLUE}🚀 Launching emulator: $SELECTED_AVD${NC}"
                /Users/jsanmartin/Library/Android/sdk/emulator/emulator -avd "$SELECTED_AVD" -no-snapshot-load > /tmp/emulator.log 2>&1 &
                EMULATOR_PID=$!
                
                echo -e "${YELLOW}⏳ Waiting for emulator to boot (this may take 2-3 minutes)...${NC}"
                sleep 20
                
                # Wait for adb to detect device
                max_attempts=120
                attempt=0
                DEVICE_DETECTED=false
                
                while [ $attempt -lt $max_attempts ]; do
                    DEVICE_COUNT=$(/Users/jsanmartin/Library/Android/sdk/platform-tools/adb devices 2>/dev/null | grep "emulator" | grep "device" | wc -l)
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
        echo -e "  /Users/jsanmartin/Library/Android/sdk/tools/bin/avdmanager create avd -n \"Pixel_4\" -k \"system-images;android-31;google_apis;arm64-v8a\""
        echo -e ""
        echo -e "${YELLOW}Or connect a physical device with USB debugging enabled${NC}"
        exit 1
    fi
fi

# Wait for device/emulator to be fully ready
echo -e "${BLUE}⏳ Waiting for device to be fully ready...${NC}"
/Users/jsanmartin/Library/Android/sdk/platform-tools/adb wait-for-device
sleep 3

# Show connected device(s)
echo -e "${GREEN}✓ Connected device(s):${NC}"
/Users/jsanmartin/Library/Android/sdk/platform-tools/adb devices -l | grep -E "device|emulator" | grep -v "List of attached"

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
/Users/jsanmartin/Library/Android/sdk/platform-tools/adb install -r "$APK_PATH"
if [ $? -ne 0 ]; then
    echo -e "${RED}❌ Error installing APK${NC}"
    echo -e "${YELLOW}Try uninstalling the app first:${NC}"
    echo -e "  adb uninstall com.vonage.video.app"
    exit 1
fi
echo -e "${GREEN}✓ APK installed successfully${NC}"

# ========== RUN TESTS ==========
echo -e "${BLUE}🧪 Running Maestro tests...${NC}"

# Check if flows directory exists
if [ ! -d "maestro/flows" ]; then
    echo -e "${RED}❌ Error: maestro/flows directory not found${NC}"
    echo -e "${YELLOW}Create the directory and add test flow YAML files${NC}"
    exit 1
fi

# Count flows
FLOW_COUNT=$(find maestro/flows -name "*.yaml" | wc -l)
if [ "$FLOW_COUNT" -eq 0 ]; then
    echo -e "${YELLOW}⚠ Warning: No test flows found in maestro/flows/${NC}"
    echo -e "${YELLOW}Add .yaml files with your tests${NC}"
    exit 0
fi

echo -e "${BLUE}Found $FLOW_COUNT test(s)${NC}"
/Users/jsanmartin/.maestro/bin/maestro test maestro/flows/

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
