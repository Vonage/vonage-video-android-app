import { type FormField } from "../components/form.js";

interface SchemaProperty {
  type?: string;
  description?: string;
  enum?: string[];
  pattern?: string;
  minimum?: number;
  maximum?: number;
  minLength?: number;
  maxLength?: number;
  properties?: Record<string, SchemaProperty>;
  required?: string[];
  additionalProperties?: boolean;
  $ref?: string;
}

interface JsonSchema {
  properties?: Record<string, SchemaProperty>;
  required?: string[];
  $defs?: Record<string, SchemaProperty>;
}

export interface FormSection {
  title: string;
  fields: FormField[];
}

/**
 * Generate sections with fields from a JSON Schema + matching data object.
 * Top-level objects become sections; leaf properties become fields.
 */
export function schemaToSections(
  schema: JsonSchema,
  data: Record<string, unknown>,
): FormSection[] {
  const sections: FormSection[] = [];
  const props = schema.properties ?? {};
  const defs = schema.$defs ?? {};

  for (const [key, prop] of Object.entries(props)) {
    const resolved = resolveRef(prop, defs);
    const value = data[key];

    if (resolved.type === "object" && resolved.properties) {
      // This becomes a section
      const fields = objectToFields(
        resolved.properties,
        defs,
        (value as Record<string, unknown>) ?? {},
        key,
      );
      sections.push({ title: formatLabel(key), fields });
    } else {
      // Top-level non-object fields go into a "General" section
      const existing = sections.find((s) => s.title === "General");
      const field = propertyToField(`${key}`, key, resolved, value);
      if (existing) {
        existing.fields.push(field);
      } else {
        sections.unshift({ title: "General", fields: [field] });
      }
    }
  }

  return sections;
}

/**
 * Flat version for backward compat — returns FormField[] with section separators.
 */
export function schemaToFields(
  schema: JsonSchema,
  data: Record<string, unknown>,
  prefix = "",
): FormField[] {
  const sections = schemaToSections(schema, data);
  const fields: FormField[] = [];

  for (const section of sections) {
    // Insert a separator field
    fields.push({
      key: `__section__${section.title}`,
      label: section.title,
      type: "section" as any,
      value: null,
    });
    fields.push(...section.fields);
  }

  return fields;
}

function objectToFields(
  properties: Record<string, SchemaProperty>,
  defs: Record<string, SchemaProperty>,
  data: Record<string, unknown>,
  parentKey: string,
): FormField[] {
  const fields: FormField[] = [];

  for (const [key, prop] of Object.entries(properties)) {
    const resolved = resolveRef(prop, defs);
    const fullKey = `${parentKey}.${key}`;
    const value = data[key];

    if (resolved.type === "object" && resolved.properties) {
      // Nested sub-object: flatten with a sub-section separator
      fields.push({
        key: `__section__${fullKey}`,
        label: formatLabel(key),
        type: "section" as any,
        value: null,
      });
      fields.push(
        ...objectToFields(resolved.properties, defs, (value as Record<string, unknown>) ?? {}, fullKey),
      );
    } else {
      fields.push(propertyToField(fullKey, key, resolved, value));
    }
  }

  return fields;
}

/**
 * Reconstruct a nested object from flat dot-key values.
 */
export function fieldsToData(values: Record<string, unknown>): Record<string, unknown> {
  const result: Record<string, unknown> = {};

  for (const [dotKey, val] of Object.entries(values)) {
    if (dotKey.startsWith("__section__")) continue;
    const parts = dotKey.split(".");
    let target: Record<string, unknown> = result;
    for (let i = 0; i < parts.length - 1; i++) {
      if (!(parts[i] in target)) target[parts[i]] = {};
      target = target[parts[i]] as Record<string, unknown>;
    }
    target[parts[parts.length - 1]] = val;
  }

  return result;
}

function resolveRef(prop: SchemaProperty, defs: Record<string, SchemaProperty>): SchemaProperty {
  if (!prop.$ref) return prop;
  const refName = prop.$ref.replace("#/$defs/", "");
  const resolved = defs[refName];
  if (!resolved) return prop;
  return resolveRef(resolved, defs);
}

function propertyToField(fullKey: string, rawKey: string, prop: SchemaProperty, value: unknown): FormField {
  const label = formatLabel(rawKey);

  if (prop.enum) {
    return { key: fullKey, label, type: "enum", value: value ?? prop.enum[0], enumValues: prop.enum, description: prop.description };
  }
  if (prop.type === "boolean") {
    return { key: fullKey, label, type: "boolean", value: value ?? false, description: prop.description };
  }
  if (prop.type === "integer") {
    return { key: fullKey, label, type: "integer", value: value ?? 0, min: prop.minimum, max: prop.maximum, description: prop.description };
  }
  if (prop.type === "string" && prop.pattern === "^#[0-9A-Fa-f]{6}$") {
    return { key: fullKey, label, type: "color", value: value ?? "#000000", pattern: prop.pattern, description: prop.description };
  }
  return { key: fullKey, label, type: "string", value: value ?? "", pattern: prop.pattern, description: prop.description };
}

function formatLabel(key: string): string {
  return key
    .replace(/([a-z])([A-Z])/g, "$1 $2")
    .replace(/-/g, " ")
    .replace(/\b\w/g, (c) => c.toUpperCase());
}
