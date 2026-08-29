package com.picanounon.back.model;

import com.fasterxml.jackson.annotation.JsonValue;

public enum TideType {
    PLEAMAR("Pleamar"),
    BAJAMAR("Bajamar");

    private final String value;

    TideType(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }

    public static TideType fromString(String text) {
        if (text == null) {
            return null;
        }
        String normalized = text.trim().toLowerCase();
        if (normalized.contains("preamar") || normalized.contains("pleamar") || normalized.contains("alta") || normalized.equalsIgnoreCase("p")) {
            return PLEAMAR;
        }
        if (normalized.contains("baixamar") || normalized.contains("bajamar") || normalized.contains("baja") || normalized.equalsIgnoreCase("b")) {
            return BAJAMAR;
        }
        throw new IllegalArgumentException("Unknown tide type: " + text);
    }
}
