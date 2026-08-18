import React, { useState } from "react";
import { Box, Text, useInput } from "ink";
import { Header, StatusBar, SectionTitle, colors } from "../components/theme.js";
import { Form } from "../components/form.js";
import { loadThemeConfig, validateThemeData, themeSchema } from "../lib/validator.js";
import { saveThemeConfig } from "../lib/file-writer.js";
import { runGradleTask } from "../lib/gradle.js";
import { schemaToFields, fieldsToData } from "../lib/schema-to-fields.js";

interface ThemeScreenProps {
  onBack: () => void;
}

type Section = "menu" | "colors-light" | "colors-dark" | "border-radius" | "typography";

// Resolve $defs references to get sub-schemas
function resolveDef(schema: any, refPath: string): any {
  const name = refPath.replace("#/$defs/", "");
  return schema.$defs?.[name] ?? {};
}

function getSubSchema(section: Section): { schema: any; dataPath: string[] } {
  const themeDef = resolveDef(themeSchema, "#/$defs/theme");
  switch (section) {
    case "colors-light": {
      const colorScheme = resolveDef(themeSchema, "#/$defs/colorScheme");
      return { schema: { ...colorScheme, $defs: themeSchema.$defs }, dataPath: ["themes", "vonage", "colors", "light"] };
    }
    case "colors-dark": {
      const colorScheme = resolveDef(themeSchema, "#/$defs/colorScheme");
      return { schema: { ...colorScheme, $defs: themeSchema.$defs }, dataPath: ["themes", "vonage", "colors", "dark"] };
    }
    case "border-radius": {
      const borderRadius = resolveDef(themeSchema, "#/$defs/borderRadius");
      return { schema: { ...borderRadius, $defs: themeSchema.$defs }, dataPath: ["themes", "vonage", "borderRadius"] };
    }
    case "typography": {
      const typography = resolveDef(themeSchema, "#/$defs/typography");
      return { schema: { ...typography, $defs: themeSchema.$defs }, dataPath: ["themes", "vonage", "typography"] };
    }
    default:
      return { schema: {}, dataPath: [] };
  }
}

function getNestedValue(obj: any, path: string[]): any {
  let current = obj;
  for (const key of path) {
    if (current == null) return {};
    current = current[key];
  }
  return current ?? {};
}

function setNestedValue(obj: any, path: string[], value: any): any {
  const clone = JSON.parse(JSON.stringify(obj));
  let target = clone;
  for (let i = 0; i < path.length - 1; i++) {
    target = target[path[i]];
  }
  target[path[path.length - 1]] = value;
  return clone;
}

export function ThemeScreen({ onBack }: ThemeScreenProps) {
  const [section, setSection] = useState<Section>("menu");
  const [status, setStatus] = useState<{ msg: string; type: "info" | "success" | "error" | "warning" } | null>(null);
  const [running, setRunning] = useState(false);

  const { data, validation } = loadThemeConfig();

  if (section === "menu") {
    return (
      <ThemeSectionMenu
        onBack={onBack}
        onSelect={setSection}
        validation={validation.valid}
        status={status}
      />
    );
  }

  const { schema, dataPath } = getSubSchema(section);
  const sectionData = getNestedValue(data, dataPath);
  const fields = schemaToFields(schema, sectionData);

  function handleSave(values: Record<string, unknown>) {
    const rebuilt = fieldsToData(values);
    const updated = setNestedValue(data, dataPath, rebuilt);

    const result = validateThemeData(updated);
    if (!result.valid) {
      setStatus({ msg: `Validation failed: ${result.errors[0]}`, type: "error" });
      return;
    }

    saveThemeConfig(updated);
    setStatus({ msg: "Saved theme.json. Running clean generateTheme...", type: "info" });
    setRunning(true);

    runGradleTask("generateTheme").then((res) => {
      if (res.success) {
        setStatus({ msg: "Theme generated successfully!", type: "success" });
      } else {
        setStatus({ msg: `Gradle failed: ${res.output.slice(0, 120)}`, type: "error" });
      }
      setRunning(false);
      setSection("menu");
    });
  }

  if (running) {
    return (
      <Box flexDirection="column">
        <Header title="Theme Editor" />
        <Box>
          <Text color={colors.accent}>⠋ </Text>
          <Text color={colors.muted}>Running Gradle generateTheme...</Text>
        </Box>
      </Box>
    );
  }

  return (
    <Box flexDirection="column">
      <Header title={`Theme: ${section}`} />
      <Form fields={fields} onSave={handleSave} onCancel={() => setSection("menu")} />
      {status && <StatusBar message={status.msg} type={status.type} />}
    </Box>
  );
}

function ThemeSectionMenu({ onBack, onSelect, validation, status }: {
  onBack: () => void;
  onSelect: (s: Section) => void;
  validation: boolean;
  status: { msg: string; type: "info" | "success" | "error" | "warning" } | null;
}) {
  const items: Array<{ key: Section; label: string }> = [
    { key: "colors-light", label: "Light Colors" },
    { key: "colors-dark", label: "Dark Colors" },
    { key: "border-radius", label: "Border Radius" },
    { key: "typography", label: "Typography" },
  ];
  const [cursor, setCursor] = useState(0);

  useInput((input, key) => {
    if (key.escape || input === "q") { onBack(); return; }
    if (key.upArrow || input === "k") setCursor((c) => Math.max(0, c - 1));
    if (key.downArrow || input === "j") setCursor((c) => Math.min(items.length - 1, c + 1));
    if (key.return) onSelect(items[cursor].key);
  });

  return (
    <Box flexDirection="column">
      <Header title="Theme Editor" />
      {!validation && <StatusBar message="Current theme.json has validation issues" type="warning" />}
      <SectionTitle>Select a section to edit</SectionTitle>
      <Box flexDirection="column" marginTop={1}>
        {items.map((item, i) => (
          <Box key={item.key}>
            <Text color={i === cursor ? colors.primary : colors.muted}>
              {i === cursor ? "❯ " : "  "}
            </Text>
            <Text color={i === cursor ? colors.text : colors.secondary}>
              {item.label}
            </Text>
          </Box>
        ))}
      </Box>
      <Box marginTop={1}>
        <Text color={colors.muted}>↑↓ navigate  ⏎ select  q/esc back</Text>
      </Box>
      {status && <StatusBar message={status.msg} type={status.type} />}
    </Box>
  );
}
