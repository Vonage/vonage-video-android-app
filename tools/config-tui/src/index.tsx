#!/usr/bin/env node
import React, { useState, useEffect } from "react";
import { render, Box, Text, useInput } from "ink";
import { Header, StatusBar, KeyHint, colors } from "./components/theme.js";
import { AppConfigScreen } from "./screens/app-config.js";
import { ThemeScreen } from "./screens/theme.js";
import { loadAppConfig, loadThemeConfig } from "./lib/validator.js";
import { runGradleTask } from "./lib/gradle.js";

type Screen = "main" | "app-config" | "theme";

function App() {
  const [screen, setScreen] = useState<Screen>("main");

  return (
    <Box flexDirection="column" paddingX={1} paddingY={1}>
      {screen === "app-config" ? (
        <AppConfigScreen onBack={() => setScreen("main")} />
      ) : screen === "theme" ? (
        <ThemeScreen onBack={() => setScreen("main")} />
      ) : (
        <MainMenu onSelect={setScreen} />
      )}
    </Box>
  );
}

const SPINNER_FRAMES = ["⠋", "⠙", "⠹", "⠸", "⠼", "⠴", "⠦", "⠧", "⠇", "⠏"];

function Spinner() {
  const [frame, setFrame] = useState(0);
  useEffect(() => {
    const timer = setInterval(() => setFrame((f) => (f + 1) % SPINNER_FRAMES.length), 80);
    return () => clearInterval(timer);
  }, []);
  return <Text color={colors.accent}>{SPINNER_FRAMES[frame]}</Text>;
}

function ElapsedTimer({ startTime }: { startTime: number }) {
  const [elapsed, setElapsed] = useState(0);
  useEffect(() => {
    const timer = setInterval(() => setElapsed(Math.floor((Date.now() - startTime) / 1000)), 1000);
    return () => clearInterval(timer);
  }, [startTime]);
  const mins = Math.floor(elapsed / 60);
  const secs = elapsed % 60;
  const display = mins > 0 ? `${mins}m ${secs}s` : `${secs}s`;
  return <Text color={colors.muted}>{display}</Text>;
}

function BuildProgress({ currentTask, startTime }: { currentTask: string; startTime: number }) {
  return (
    <Box flexDirection="column" marginTop={1} marginLeft={2}>
      <Box gap={1}>
        <Spinner />
        <Text color={colors.text} bold>Building...</Text>
        <ElapsedTimer startTime={startTime} />
      </Box>
      <Box marginTop={0} marginLeft={3}>
        <Text color={colors.secondary} wrap="truncate-end">
          {currentTask || "Starting Gradle..."}
        </Text>
      </Box>
    </Box>
  );
}

function MainMenu({ onSelect }: { onSelect: (s: Screen) => void }) {
  const [cursor, setCursor] = useState(0);
  const [status, setStatus] = useState<{ msg: string; type: "info" | "success" | "error" } | null>(null);
  const [launching, setLaunching] = useState(false);
  const [currentTask, setCurrentTask] = useState("");
  const [startTime, setStartTime] = useState(0);

  // Quick validation status
  let appValid = true;
  let themeValid = true;
  try { appValid = loadAppConfig().validation.valid; } catch { appValid = false; }
  try { themeValid = loadThemeConfig().validation.valid; } catch { themeValid = false; }

  type MenuItem = { key: string; label: string; desc: string; valid?: boolean };
  const items: MenuItem[] = [
    { key: "app-config", label: "App Config", desc: "Feature toggles, base URL, settings", valid: appValid },
    { key: "theme", label: "Theme", desc: "Colors, border radius, typography", valid: themeValid },
    { key: "launch", label: "Launch App", desc: "Build & install debug APK on device" },
  ];

  useInput((input, key) => {
    if (launching) return;
    if (key.escape || input === "q") {
      process.exit(0);
    }
    if (key.upArrow || input === "k") {
      setCursor((c) => Math.max(0, c - 1));
    } else if (key.downArrow || input === "j") {
      setCursor((c) => Math.min(items.length - 1, c + 1));
    } else if (key.return) {
      const selected = items[cursor];
      if (selected.key === "launch") {
        setLaunching(true);
        setStartTime(Date.now());
        setCurrentTask("");
        setStatus(null);

        runGradleTask("installDebug", (line) => {
          // Show the most relevant Gradle output (task names, progress)
          if (line.startsWith("> Task") || line.startsWith("> Configure") || line.includes("BUILD")) {
            setCurrentTask(line);
          } else if (line.startsWith(":") || line.includes("install")) {
            setCurrentTask(line);
          }
        }).then((res) => {
          if (res.success) {
            const secs = (res.durationMs / 1000).toFixed(1);
            setStatus({ msg: `App installed successfully! (${secs}s)`, type: "success" });
          } else {
            setStatus({ msg: `Build failed: ${res.output.slice(-150)}`, type: "error" });
          }
          setLaunching(false);
          setCurrentTask("");
        });
      } else {
        onSelect(selected.key as Screen);
      }
    }
  });

  return (
    <Box flexDirection="column" paddingX={1}>
      <Box marginBottom={1} flexDirection="column">
        <Text color={colors.primary} bold>
          {"  ◆  Vonage Video Config"}
        </Text>
        <Text color={colors.muted}>
          {"     Edit config & theme, validate, and regenerate"}
        </Text>
      </Box>

      <Box flexDirection="column">
        {items.map((item, i) => {
          const active = i === cursor;
          return (
            <Box key={item.key} gap={1}>
              <Text color={active ? colors.primary : colors.muted}>
                {active ? "❯" : " "}
              </Text>
              <Text color={active ? colors.text : colors.secondary} bold={active}>
                {item.label}
              </Text>
              <Text color={colors.muted}> — {item.desc}</Text>
              {"valid" in item && item.valid !== undefined && (
                <Text color={item.valid ? colors.success : colors.error}>
                  {" "}{item.valid ? "✓" : "✗"}
                </Text>
              )}
              {item.key === "launch" && (
                <Text color={colors.accent}> {" ▶"}</Text>
              )}
            </Box>
          );
        })}
      </Box>

      {launching && <BuildProgress currentTask={currentTask} startTime={startTime} />}

      {status && <StatusBar message={status.msg} type={status.type} />}

      <KeyHint keys={[
        { key: "↑↓", label: "navigate" },
        { key: "⏎", label: "select" },
        { key: "q", label: "quit" },
      ]} />
    </Box>
  );
}

render(<App />);
