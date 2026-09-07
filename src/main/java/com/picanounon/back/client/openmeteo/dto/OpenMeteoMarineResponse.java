package com.picanounon.back.client.openmeteo.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class OpenMeteoMarineResponse {

    private HourlyMarine hourly;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class HourlyMarine {
        private List<String> time;

        @JsonProperty("wave_height")
        private List<Double> waveHeight;

        @JsonProperty("wave_period")
        private List<Double> wavePeriod;

        @JsonProperty("sea_surface_temperature")
        private List<Double> seaSurfaceTemperature;
    }
}
