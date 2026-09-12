package com.picanounon.back.service.scoring;

import com.picanounon.back.dto.scoring.MarineConditionsDTO;
import com.picanounon.back.dto.scoring.ScoreResultDTO;
import com.picanounon.back.model.Species;

public interface SpeciesScoringStrategy {
    Species getSpecies();
    ScoreResultDTO calculateScore(MarineConditionsDTO conditions);
}
