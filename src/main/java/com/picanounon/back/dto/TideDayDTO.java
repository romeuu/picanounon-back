package com.picanounon.back.dto;

import java.util.List;

import com.picanounon.back.dto.response.TideResponse;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TideDayDTO {
    private List<TideResponse> tides;
    private Integer dailyCoefficient;
    private List<Integer> cycleCoefficient;
}
