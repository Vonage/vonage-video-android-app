import React, { useState } from "react";
import { Box, Text, useInput, useStdout } from "ink";
import TextInput from "ink-text-input";
import { colors } from "./theme.js";

export interface FormField {
  key: string;
  label: string;
  type: "boolean" | "string" | "enum" | "integer" | "color" | "section";
  value: unknown;
  enumValues?: string[];
  description?: string;
  min?: number;
  max?: number;
  pattern?: string;
}

interface FormProps {
  fields: FormField[];
  onSave: (values: Record<string, unknown>) => void;
  onCancel: () => void;
}

export function Form({ fields, onSave, onCancel }: FormProps) {
  // Find first non-section field for initial cursor
  const firstEditable = fields.findIndex((f) => f.type !== "section");
  const [cursor, setCursor] = useState(firstEditable >= 0 ? firstEditable : 0);
  const [values, setValues] = useState<Record<string, unknown>>(
    Object.fromEntries(fields.filter((f) => f.type !== "section").map((f) => [f.key, f.value]))
  );
  const [editing, setEditing] = useState(false);
  const [editBuffer, setEditBuffer] = useState("");

  const { stdout } = useStdout();
  const termWidth = stdout?.columns ?? 80;
  const panelWidth = Math.min(34, Math.max(24, Math.floor(termWidth * 0.3)));

  const current = fields[cursor];

  function nextEditable(from: number, direction: 1 | -1): number {
    let i = from + direction;
    while (i >= 0 && i < fields.length) {
      if (fields[i].type !== "section") return i;
      i += direction;
    }
    return from; // stay put if nothing found
  }

  useInput((input, key) => {
    if (editing) {
      if (key.escape) {
        setEditing(false);
        return;
      }
      if (key.return) {
        const field = fields[cursor];
        let newValue: unknown = editBuffer;
        if (field.type === "integer") {
          const n = parseInt(editBuffer, 10);
          if (!isNaN(n)) newValue = n;
          else return;
        }
        setValues((v) => ({ ...v, [field.key]: newValue }));
        setEditing(false);
        return;
      }
      return;
    }

    if (key.escape || input === "q") {
      onCancel();
      return;
    }

    if (key.upArrow || input === "k") {
      setCursor((c) => nextEditable(c, -1));
    } else if (key.downArrow || input === "j") {
      setCursor((c) => nextEditable(c, 1));
    } else if (input === "s" || (key.ctrl && input === "s")) {
      onSave(values);
    } else if (key.return || input === " ") {
      const field = fields[cursor];
      if (field.type === "boolean") {
        setValues((v) => ({ ...v, [field.key]: !v[field.key] }));
      } else if (field.type === "enum") {
        const vals = field.enumValues || [];
        const idx = vals.indexOf(String(values[field.key]));
        const next = vals[(idx + 1) % vals.length];
        setValues((v) => ({ ...v, [field.key]: next }));
      } else {
        setEditing(true);
        setEditBuffer(String(values[field.key] ?? ""));
      }
    }
  });

  return (
    <Box flexDirection="row">
      {/* Left: all form fields rendered — Ink handles terminal overflow */}
      <Box flexDirection="column" flexGrow={1} flexShrink={1}>
        {fields.map((field, i) => {
          const isActive = i === cursor;

          // Render section headers
          if (field.type === "section") {
            return (
              <Box key={field.key} marginTop={i > 0 ? 1 : 0}>
                <Text color={colors.accent} bold>
                  {"▸ "}
                </Text>
                <Text color={colors.text} dimColor bold>
                  {field.label}
                </Text>
              </Box>
            );
          }

          const val = values[field.key];
          return (
            <Box key={field.key}>
              <Text color={isActive ? colors.primary : colors.muted}>
                {isActive ? "❯ " : "  "}
              </Text>
              <Box width={28} flexShrink={0}>
                <Text color={isActive ? colors.text : colors.secondary} wrap="truncate-end">
                  {field.label}
                </Text>
              </Box>
              <Text> </Text>
              {editing && isActive ? (
                <Box>
                  <Text color={colors.accent}>{"▎"}</Text>
                  <TextInput
                    value={editBuffer}
                    onChange={setEditBuffer}
                    showCursor
                  />
                </Box>
              ) : (
                <FieldValue field={field} value={val} active={isActive} />
              )}
            </Box>
          );
        })}
        <Box marginTop={1}>
          <Text color={colors.muted}>
            ↑↓ navigate  ⏎/space toggle  s/ctrl+s save  q/esc back
          </Text>
        </Box>
      </Box>

      {/* Right: description panel */}
      <Box
        flexDirection="column"
        width={panelWidth}
        marginLeft={1}
        borderStyle="single"
        borderColor={colors.border}
        paddingX={1}
        paddingY={0}
        flexShrink={0}
      >
        <Text color={colors.accent} bold>
          {"◇ Details"}
        </Text>
        <Box marginTop={1} flexDirection="column">
          <Text color={colors.text} bold wrap="wrap">
            {current?.label ?? ""}
          </Text>
          <Box marginTop={1}>
            <Text color={colors.secondary} wrap="wrap">
              {current?.description || "No description."}
            </Text>
          </Box>
          {current?.type === "enum" && current.enumValues && (
            <Box marginTop={1} flexDirection="column">
              <Text color={colors.accent} dimColor>Options:</Text>
              {current.enumValues.map((v) => (
                <Text key={v} color={v === String(values[current.key]) ? colors.text : colors.muted}>
                  {v === String(values[current.key]) ? " ● " : " ○ "}{v}
                </Text>
              ))}
            </Box>
          )}
          {current?.type === "integer" && (
            <Box marginTop={1}>
              <Text color={colors.muted}>
                Range: {current.min ?? "–∞"} – {current.max ?? "∞"}
              </Text>
            </Box>
          )}
          {current?.type === "color" && (
            <Box marginTop={1} flexDirection="column">
              <Text color={colors.muted}>Format: #RRGGBB</Text>
              <Box marginTop={0}>
                <Text color={String(values[current.key])}>{"████████"}</Text>
              </Box>
            </Box>
          )}
          {current?.type === "boolean" && (
            <Box marginTop={1}>
              <Text color={colors.muted}>⏎/space to toggle</Text>
            </Box>
          )}
        </Box>
        {/* Position indicator */}
        <Box marginTop={1}>
          <Text color={colors.muted}>
            {cursor + 1}/{fields.length}
          </Text>
        </Box>
      </Box>
    </Box>
  );
}

function FieldValue({ field, value, active }: { field: FormField; value: unknown; active: boolean }) {
  if (field.type === "boolean") {
    const on = value === true;
    return (
      <Text color={on ? colors.success : colors.error}>
        {on ? "● enabled" : "○ disabled"}
      </Text>
    );
  }
  if (field.type === "enum") {
    return (
      <Text color={active ? colors.accent : colors.muted}>
        {String(value)}
      </Text>
    );
  }
  if (field.type === "color") {
    return (
      <Box>
        <Text color={String(value)}>{"██"}</Text>
        <Text color={active ? colors.text : colors.muted}> {String(value)}</Text>
      </Box>
    );
  }
  return (
    <Text color={active ? colors.text : colors.muted} wrap="truncate-end">
      {String(value)}
    </Text>
  );
}
