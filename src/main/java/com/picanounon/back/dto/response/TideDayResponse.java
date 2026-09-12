package com.picanounon.back.dto.response;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TideDayResponse {
    private List<TideResponse> tides;
    private Integer dailyCoefficient;
    private List<Integer> cycleCoefficient;
}
