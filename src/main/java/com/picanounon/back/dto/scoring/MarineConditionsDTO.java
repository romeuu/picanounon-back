package com.picanounon.back.dto.scoring;

import com.picanounon.back.model.TidePhase;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MarineConditionsDTO {
    private Double waveHeight;
    private Double wavePeriod;
    private Double windSpeed;
    private TidePhase tidePhase;
    private Double tideHeight;
    private Integer tideCoefficient;
    private Boolean isCrepuscular;
    private Boolean isDaylight;
    private Double waterTemperature;
    private Double temperature;
}