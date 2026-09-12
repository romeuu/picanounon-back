package com.picanounon.back.dto.scoring;

import com.picanounon.back.model.SafetyLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ScoreResultDTO {
    private Integer score;
    private Boolean isSafe;
    private SafetyLevel safetyLevel;
    private String verdict;
}
