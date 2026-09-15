package com.picanounon.back.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MarineWeatherDTO {
    private List<String> time;
    private List<Double> waveHeight;
    private List<Double> wavePeriod;
    private List<Double> windSpeed;
    private List<Double> windDirection;
    private List<Integer> isDay;
    private List<Double> seaTemperature;
    private List<Double> temperature;
    private List<String> sunrise;
    private List<String> sunset;
}
