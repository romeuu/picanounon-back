package com.picanounon.back.model;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum TideType {
    PLEAMAR("Pleamar"),
    BAJAMAR("Bajamar");

    @JsonValue
    private final String value;

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
