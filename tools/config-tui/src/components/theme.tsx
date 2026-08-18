import React from "react";
import { Box, Text } from "ink";

// Claude-inspired palette: soft earth tones, warm beige bg concept → terminal-adapted
export const colors = {
  primary: "#D97706",      // warm amber
  secondary: "#6B7280",    // gray-500
  accent: "#F59E0B",       // amber highlight
  success: "#10B981",      // emerald
  error: "#EF4444",        // red
  warning: "#F59E0B",      // amber
  muted: "#9CA3AF",        // gray-400
  text: "#F3F4F6",         // gray-100
  border: "#4B5563",       // gray-600
  surface: "#1F2937",      // gray-800
  highlight: "#FEF3C7",    // amber-50
} as const;

export function Header({ title }: { title: string }) {
  return (
    <Box flexDirection="column" marginBottom={1}>
      <Box>
        <Text color={colors.primary} bold>
          {"◆ "}
        </Text>
        <Text color={colors.text} bold>
          {title}
        </Text>
      </Box>
      <Box>
        <Text color={colors.border}>
          {"─".repeat(Math.min(60, title.length + 10))}
        </Text>
      </Box>
    </Box>
  );
}

export function StatusBar({ message, type = "info" }: { message: string; type?: "info" | "success" | "error" | "warning" }) {
  const colorMap = { info: colors.muted, success: colors.success, error: colors.error, warning: colors.warning };
  const iconMap = { info: "●", success: "✓", error: "✗", warning: "⚠" };
  return (
    <Box marginTop={1}>
      <Text color={colorMap[type]}>
        {iconMap[type]} {message}
      </Text>
    </Box>
  );
}

export function SectionTitle({ children }: { children: string }) {
  return (
    <Box marginTop={1} marginBottom={0}>
      <Text color={colors.accent} bold>
        {"▸ "}
      </Text>
      <Text color={colors.text} dimColor>
        {children}
      </Text>
    </Box>
  );
}

export function KeyHint({ keys }: { keys: Array<{ key: string; label: string }> }) {
  return (
    <Box marginTop={1} gap={2}>
      {keys.map(({ key, label }) => (
        <Box key={key}>
          <Text color={colors.primary} bold>
            {key}
          </Text>
          <Text color={colors.muted}> {label}</Text>
        </Box>
      ))}
    </Box>
  );
}
