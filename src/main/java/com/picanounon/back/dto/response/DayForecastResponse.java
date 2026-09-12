package com.picanounon.back.dto.response;

import com.picanounon.back.model.Species;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DayForecastResponse {
    private Long portId;
    private String portName;
    private String date;
    private Species selectedSpecies;
    private Integer dailyCoefficient;
    private List<Integer> cycleCoefficients;
    private List<HourlyForecastResponse> hourlyForecasts;
}