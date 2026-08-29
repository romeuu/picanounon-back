package com.picanounon.back.model;

import com.fasterxml.jackson.annotation.JsonValue;

public enum Zone {
    RIAS_BAIXAS("Rías Baixas"),
    COSTA_DA_MORTE("Costa da Morte"),
    RIAS_ALTAS("Rías Altas"),
    CANTABRICO("Cantábrico"),
    FERROL_BARES("Ferrol-Bares"),
    ARTABRO("Ártabro");

    private final String value;

    Zone(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }

    public static Zone fromValue(String text) {
        for (Zone z : Zone.values()) {
            if (z.value.equalsIgnoreCase(text)) {
                return z;
            }
        }
        throw new IllegalArgumentException("Unknown zone: " + text);
    }
}
