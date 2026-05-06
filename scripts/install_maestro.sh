#!/bin/bash
# Script para instalar Maestro y sus dependencias
# Uso: ./scripts/install_maestro.sh
# Puede ser invocado standalone o desde run_maestro_tests.sh via source

# Output colors
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m'

# ========== SETUP JAVA ==========
setup_java() {
    echo -e "${BLUE}☕ Configuring Java...${NC}"

    # Honor existing JAVA_HOME if already set
    if [ -n "$JAVA_HOME" ]; then
        echo -e "${GREEN}✓ JAVA_HOME already set: $JAVA_HOME${NC}"
        return 0
    fi

    if [ "$(uname)" = "Darwin" ]; then
        # macOS: use java_home utility
        JAVA_17=$(/usr/libexec/java_home -v17 2>/dev/null || echo "")
        if [ -n "$JAVA_17" ]; then
            export JAVA_HOME="$JAVA_17"
        else
            export JAVA_HOME=$(/usr/libexec/java_home 2>/dev/null || echo "")
        fi
    else
        # Linux / CI: resolve from java binary
        JAVA_BIN=$(which java 2>/dev/null || echo "")
        if [ -n "$JAVA_BIN" ]; then
            JAVA_REAL=$(readlink -f "$JAVA_BIN" 2>/dev/null || readlink "$JAVA_BIN" 2>/dev/null || echo "$JAVA_BIN")
            export JAVA_HOME=$(dirname "$(dirname "$JAVA_REAL")")
        fi
    fi

    if [ -z "$JAVA_HOME" ]; then
        echo -e "${RED}❌ Error: Java not found. Maestro requires Java 17.${NC}"
        if [ "$(uname)" = "Darwin" ]; then
            echo -e "${YELLOW}Install with: brew install openjdk@17${NC}"
        else
            echo -e "${YELLOW}Install with: sudo apt install openjdk-17-jdk${NC}"
        fi
        return 1
    fi
    echo -e "${GREEN}✓ JAVA_HOME: $JAVA_HOME${NC}"
}

# ========== SETUP ANDROID SDK ==========
setup_android_sdk() {
    echo -e "${BLUE}🤖 Configuring Android SDK...${NC}"
    if [ -z "$ANDROID_HOME" ]; then
        # Honor ANDROID_SDK_ROOT if set
        if [ -n "$ANDROID_SDK_ROOT" ]; then
            export ANDROID_HOME="$ANDROID_SDK_ROOT"
        # Try common SDK locations
        elif [ -d "$HOME/Library/Android/sdk" ]; then
            export ANDROID_HOME="$HOME/Library/Android/sdk"
        elif [ -d "$HOME/Android/Sdk" ]; then
            export ANDROID_HOME="$HOME/Android/Sdk"
        elif [ -d "/usr/local/lib/android/sdk" ]; then
            export ANDROID_HOME="/usr/local/lib/android/sdk"
        else
            echo -e "${RED}❌ Error: Android SDK not found${NC}"
            echo -e "${YELLOW}Set ANDROID_HOME or ANDROID_SDK_ROOT, or install Android SDK from https://developer.android.com/studio${NC}"
            return 1
        fi
    fi
    echo -e "${GREEN}✓ ANDROID_HOME: $ANDROID_HOME${NC}"
}

# ========== SETUP PATH ==========
setup_path() {
    export PATH="$ANDROID_HOME/platform-tools:$HOME/.maestro/bin:$PATH"
}

# ========== VERIFY ADB ==========
verify_adb() {
    echo -e "${BLUE}📱 Verifying adb...${NC}"
    ADB_PATH="$ANDROID_HOME/platform-tools/adb"
    if [ ! -f "$ADB_PATH" ]; then
        echo -e "${RED}❌ Error: adb not found at $ADB_PATH${NC}"
        return 1
    fi
    echo -e "${GREEN}✓ adb found: $ADB_PATH${NC}"
}

# ========== INSTALL/VERIFY MAESTRO ==========
install_maestro() {
    echo -e "${BLUE}🧪 Verifying Maestro CLI...${NC}"
    MAESTRO_PATH="$HOME/.maestro/bin/maestro"
    if [ ! -f "$MAESTRO_PATH" ]; then
        echo -e "${YELLOW}📦 Maestro is not installed. Installing...${NC}"
        curl -fsSL "https://get.maestro.mobile.dev" | bash
        if [ $? -eq 0 ]; then
            echo -e "${GREEN}✓ Maestro installed successfully${NC}"
        else
            echo -e "${RED}❌ Error installing Maestro${NC}"
            return 1
        fi
    else
        MAESTRO_VERSION=$("$MAESTRO_PATH" --version 2>/dev/null || echo "unknown")
        echo -e "${GREEN}✓ Maestro found: $MAESTRO_PATH ($MAESTRO_VERSION)${NC}"
    fi
}

# ========== MAIN ==========
install_all() {
    setup_java || return 1
    setup_android_sdk || return 1
    setup_path
    verify_adb || return 1
    install_maestro || return 1

    echo -e ""
    echo -e "${GREEN}═══════════════════════════════════════${NC}"
    echo -e "${GREEN}✅ Maestro and dependencies ready${NC}"
    echo -e "${GREEN}═══════════════════════════════════════${NC}"
}

# Si se ejecuta directamente (no sourced), correr install_all
if [[ "${BASH_SOURCE[0]}" == "${0}" ]]; then
    install_all
fi
