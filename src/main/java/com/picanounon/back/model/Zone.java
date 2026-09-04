package com.picanounon.back.model;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Zone {
    RIAS_BAIXAS("Rías Baixas"),
    COSTA_DA_MORTE("Costa da Morte"),
    RIAS_ALTAS("Rías Altas"),
    CANTABRICO("Cantábrico"),
    FERROL_BARES("Ferrol-Bares"),
    ARTABRO("Ártabro");

    @JsonValue
    private final String value;

    public static Zone fromValue(String text) {
        for (Zone z : Zone.values()) {
            if (z.value.equalsIgnoreCase(text)) {
                return z;
            }
        }
        throw new IllegalArgumentException("Unknown zone: " + text);
    }
}
