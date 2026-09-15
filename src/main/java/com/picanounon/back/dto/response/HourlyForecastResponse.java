package com.picanounon.back.dto.response;

import com.picanounon.back.model.SafetyLevel;
import com.picanounon.back.model.TidePhase;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HourlyForecastResponse {
    private String time;
    private String dateTime;
    private Double waveHeight;
    private Double wavePeriod;
    private Double windSpeed;
    private Double windDirection;
    private Double seaTemperature;
    private Double temperature;
    private Double tideHeight;
    private Integer tideCoefficient;
    private Boolean isTideRising;
    private TidePhase tidePhase;
    private Boolean isSafe;
    private SafetyLevel safetyLevel;
    private Integer score;
    private String verdict;
    private Integer scoreSargos;
    private Integer scoreRobaliza;
    private Integer scoreAgullas;
    private Integer scoreXardas;
}