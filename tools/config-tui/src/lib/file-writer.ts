import { writeFileSync } from "node:fs";
import { resolve } from "node:path";
import { configRoot } from "./validator.js";

export function saveAppConfig(data: Record<string, unknown>): void {
  const json = JSON.stringify(data, null, 2) + "\n";
  writeFileSync(resolve(configRoot, "app-config.json"), json, "utf-8");
}

export function saveThemeConfig(data: Record<string, unknown>): void {
  const json = JSON.stringify(data, null, 2) + "\n";
  writeFileSync(resolve(configRoot, "theme.json"), json, "utf-8");
}
