import Ajv2020, { type ValidateFunction, type ErrorObject } from "ajv/dist/2020.js";
import { readFileSync } from "node:fs";
import { resolve, dirname } from "node:path";
import { fileURLToPath } from "node:url";

const __dirname = dirname(fileURLToPath(import.meta.url));
// From src/lib/ → up to config-tui → up to tools → up to project root → config/
const configRoot = resolve(__dirname, "../../../../config");

const ajv = new Ajv2020({ allErrors: true, strict: false });

function loadSchema(filename: string) {
  const raw = readFileSync(resolve(configRoot, filename), "utf-8");
  return JSON.parse(raw);
}

const appConfigSchema = loadSchema("app-config.schema.json");
const themeSchema = loadSchema("theme.schema.json");

const validateAppConfig: ValidateFunction = ajv.compile(appConfigSchema);
const validateTheme: ValidateFunction = ajv.compile(themeSchema);

export interface ValidationResult {
  valid: boolean;
  errors: string[];
}

function formatErrors(errors: ErrorObject[] | null | undefined): string[] {
  if (!errors) return [];
  return errors.map((e) => {
    const path = e.instancePath || "/";
    return `${path}: ${e.message}`;
  });
}

export function validateAppConfigData(data: unknown): ValidationResult {
  const valid = validateAppConfig(data) as boolean;
  return { valid, errors: formatErrors(validateAppConfig.errors) };
}

export function validateThemeData(data: unknown): ValidationResult {
  const valid = validateTheme(data) as boolean;
  return { valid, errors: formatErrors(validateTheme.errors) };
}

export function loadAppConfig(): { data: Record<string, unknown>; validation: ValidationResult } {
  const raw = readFileSync(resolve(configRoot, "app-config.json"), "utf-8");
  const data = JSON.parse(raw);
  const validation = validateAppConfigData(data);
  return { data, validation };
}

export function loadThemeConfig(): { data: Record<string, unknown>; validation: ValidationResult } {
  const raw = readFileSync(resolve(configRoot, "theme.json"), "utf-8");
  const data = JSON.parse(raw);
  const validation = validateThemeData(data);
  return { data, validation };
}

export { configRoot, appConfigSchema, themeSchema };
