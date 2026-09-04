"""JSON Schema → flat list of FormField."""

from __future__ import annotations

import re
from dataclasses import dataclass, field
from typing import Any, Optional


@dataclass
class FormField:
    key: str
    label: str
    type: str  # "boolean" | "string" | "enum" | "integer" | "color" | "section"
    value: Any = None
    enum_values: Optional[list[str]] = None
    description: Optional[str] = None
    min: Optional[int] = None
    max: Optional[int] = None
    pattern: Optional[str] = None


_CAMEL_SPLIT = re.compile(r"([a-z])([A-Z])")

_HEX_COLOR_PATTERNS = {
    "^#[0-9A-Fa-f]{6}$",
    "^#([0-9A-Fa-f]{6}|[0-9A-Fa-f]{8})$",
}


def _is_hex_color_pattern(pattern: Optional[str]) -> bool:
    return pattern in _HEX_COLOR_PATTERNS


def _format_label(key: str) -> str:
    result = _CAMEL_SPLIT.sub(r"\1 \2", key).replace("-", " ")
    return " ".join(w[:1].upper() + w[1:] for w in result.split(" ") if w)


def _resolve_ref(prop: dict[str, Any], defs: dict[str, Any]) -> dict[str, Any]:
    seen: set[str] = set()
    while isinstance(prop, dict) and "$ref" in prop:
        ref = prop["$ref"]
        if ref in seen:
            return prop
        seen.add(ref)
        name = ref.replace("#/$defs/", "")
        resolved = defs.get(name)
        if not resolved:
            return prop
        prop = resolved
    return prop


def _property_to_field(full_key: str, raw_key: str, prop: dict[str, Any], value: Any) -> FormField:
    label = _format_label(raw_key)
    desc = prop.get("description")

    if "enum" in prop:
        enum_vals = list(prop["enum"])
        return FormField(
            key=full_key,
            label=label,
            type="enum",
            value=value if value is not None else enum_vals[0],
            enum_values=enum_vals,
            description=desc,
        )
    if prop.get("type") == "boolean":
        return FormField(
            key=full_key,
            label=label,
            type="boolean",
            value=bool(value) if value is not None else False,
            description=desc,
        )
    if prop.get("type") == "integer":
        return FormField(
            key=full_key,
            label=label,
            type="integer",
            value=value if value is not None else 0,
            min=prop.get("minimum"),
            max=prop.get("maximum"),
            description=desc,
        )
    if prop.get("type") == "number":
        return FormField(
            key=full_key,
            label=label,
            type="integer",
            value=value if value is not None else 0,
            min=prop.get("minimum"),
            max=prop.get("maximum"),
            description=desc,
        )
    if prop.get("type") == "string" and _is_hex_color_pattern(prop.get("pattern")):
        return FormField(
            key=full_key,
            label=label,
            type="color",
            value=value if value is not None else "#000000",
            pattern=prop.get("pattern"),
            description=desc,
        )
    return FormField(
        key=full_key,
        label=label,
        type="string",
        value=value if value is not None else "",
        pattern=prop.get("pattern"),
        description=desc,
    )


def _object_to_fields(
    properties: dict[str, Any],
    defs: dict[str, Any],
    data: dict[str, Any],
    parent_key: str,
) -> list[FormField]:
    fields: list[FormField] = []
    for key, prop in properties.items():
        resolved = _resolve_ref(prop, defs)
        full_key = f"{parent_key}.{key}"
        value = data.get(key) if isinstance(data, dict) else None
        if resolved.get("type") == "object" and "properties" in resolved:
            fields.append(FormField(
                key=f"__section__{full_key}",
                label=_format_label(key),
                type="section",
            ))
            fields.extend(
                _object_to_fields(resolved["properties"], defs, value or {}, full_key)
            )
        else:
            fields.append(_property_to_field(full_key, key, resolved, value))
    return fields


def schema_to_fields(schema: dict[str, Any], data: dict[str, Any]) -> list[FormField]:
    """Flatten a schema into an ordered list of fields with 'section' separators.

    Optional object properties (not in the schema's ``required`` list) that are absent
    from ``data`` are skipped entirely, rather than materialized as an empty section —
    otherwise saving would write out an empty object that fails the sub-schema's own
    ``required`` fields (e.g. platform-specific sections like ``localizationSettings``
    that Android's app-config.json never populates).
    """
    props = schema.get("properties", {}) or {}
    defs = schema.get("$defs", {}) or {}
    required = set(schema.get("required", []) or [])

    general: list[FormField] = []
    sections: list[tuple[str, list[FormField]]] = []

    for key, prop in props.items():
        resolved = _resolve_ref(prop, defs)
        is_present = isinstance(data, dict) and key in data
        if resolved.get("type") == "object" and "properties" in resolved:
            if key not in required and not is_present:
                continue
            value = data.get(key) if isinstance(data, dict) else None
            section_fields = _object_to_fields(resolved["properties"], defs, value or {}, key)
            sections.append((_format_label(key), section_fields))
        else:
            value = data.get(key) if isinstance(data, dict) else None
            general.append(_property_to_field(key, key, resolved, value))

    out: list[FormField] = []
    if general:
        out.append(FormField(key="__section__General", label="General", type="section"))
        out.extend(general)
    for title, section_fields in sections:
        out.append(FormField(key=f"__section__{title}", label=title, type="section"))
        out.extend(section_fields)
    return out


def fields_to_data(values: dict[str, Any]) -> dict[str, Any]:
    """Reconstruct a nested object from flat ``a.b.c`` keys."""
    result: dict[str, Any] = {}
    for dot_key, val in values.items():
        if dot_key.startswith("__section__"):
            continue
        parts = dot_key.split(".")
        target: dict[str, Any] = result
        for p in parts[:-1]:
            existing = target.get(p)
            if not isinstance(existing, dict):
                target[p] = {}
            target = target[p]  # type: ignore[assignment]
        target[parts[-1]] = val
    return result
