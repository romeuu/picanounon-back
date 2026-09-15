package com.picanounon.back.client.openmeteo.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class OpenMeteoWeatherResponse {

    private HourlyWeather hourly;
    private DailyWeather daily;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class HourlyWeather {
        private List<String> time;

        @JsonProperty("wind_speed_10m")
        private List<Double> windSpeed10m;

        @JsonProperty("wind_direction_10m")
        private List<Double> windDirection10m;

        @JsonProperty("is_day")
        private List<Integer> isDay;

        @JsonProperty("temperature_2m")
        private List<Double> temperature2m;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class DailyWeather {
        private List<String> time;
        private List<String> sunrise;
        private List<String> sunset;
    }
}
