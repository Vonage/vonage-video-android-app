import React, { useState } from "react";
import { Box, Text } from "ink";
import { Header, StatusBar, colors } from "../components/theme.js";
import { Form } from "../components/form.js";
import { loadAppConfig, validateAppConfigData, appConfigSchema } from "../lib/validator.js";
import { saveAppConfig } from "../lib/file-writer.js";
import { runGradleTask } from "../lib/gradle.js";
import { schemaToFields, fieldsToData } from "../lib/schema-to-fields.js";

interface AppConfigScreenProps {
  onBack: () => void;
}

type Phase = "form" | "running";

export function AppConfigScreen({ onBack }: AppConfigScreenProps) {
  const [status, setStatus] = useState<{ msg: string; type: "info" | "success" | "error" | "warning" } | null>(null);
  const [phase, setPhase] = useState<Phase>("form");

  const { data, validation } = loadAppConfig();
  const fields = schemaToFields(appConfigSchema, data);

  function handleSave(values: Record<string, unknown>) {
    const config = fieldsToData(values);

    const result = validateAppConfigData(config);
    if (!result.valid) {
      setStatus({ msg: `Validation failed: ${result.errors[0]}`, type: "error" });
      return;
    }

    saveAppConfig(config);
    setStatus({ msg: "Saved app-config.json. Running clean generateVonageConfig...", type: "info" });
    setPhase("running");

    runGradleTask("generateVonageConfig").then((res) => {
      if (res.success) {
        setStatus({ msg: "Config generated successfully!", type: "success" });
      } else {
        setStatus({ msg: `Gradle failed: ${res.output.slice(0, 120)}`, type: "error" });
      }
      setPhase("form");
    });
  }

  return (
    <Box flexDirection="column">
      <Header title="App Configuration" />
      {!validation.valid && (
        <StatusBar message={`Current file has issues: ${validation.errors[0]}`} type="warning" />
      )}
      {phase === "form" ? (
        <Form fields={fields} onSave={handleSave} onCancel={onBack} />
      ) : (
        <Box>
          <Text color={colors.accent}>⠋ </Text>
          <Text color={colors.muted}>Running Gradle task...</Text>
        </Box>
      )}
      {status && <StatusBar message={status.msg} type={status.type} />}
    </Box>
  );
}
