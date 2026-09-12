package com.picanounon.back.service.scoring;

import com.picanounon.back.dto.scoring.MarineConditionsDTO;
import com.picanounon.back.dto.scoring.ScoreResultDTO;
import com.picanounon.back.model.SafetyLevel;
import com.picanounon.back.model.Species;
import org.springframework.stereotype.Service;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

@Service
public class ScoringService {

    private final SafetyEvaluator safetyEvaluator;
    private final Map<Species, SpeciesScoringStrategy> strategies = new EnumMap<>(Species.class);

    public ScoringService(SafetyEvaluator safetyEvaluator, List<SpeciesScoringStrategy> strategyList) {
        this.safetyEvaluator = safetyEvaluator;
        for (SpeciesScoringStrategy strategy : strategyList) {
            this.strategies.put(strategy.getSpecies(), strategy);
        }
    }

    public ScoreResultDTO calculateScore(MarineConditionsDTO conditions, Species species) {
        double hs = conditions.getWaveHeight() != null ? conditions.getWaveHeight() : 0.0;
        double tp = conditions.getWavePeriod() != null ? conditions.getWavePeriod() : 9.0;

        SafetyLevel safetyLevel = safetyEvaluator.evaluateSafety(hs, tp);
        if (safetyLevel == SafetyLevel.PERIGO_EXTREMO) {
            return ScoreResultDTO.builder()
                    .score(10)
                    .isSafe(false)
                    .safetyLevel(SafetyLevel.PERIGO_EXTREMO)
                    .verdict("Mar perigoso. Risco alto de golpe de mar na pedra.")
                    .build();
        }

        Species targetSpecies = species != null ? species : Species.SARGOS;
        SpeciesScoringStrategy strategy = strategies.getOrDefault(targetSpecies, strategies.get(Species.SARGOS));

        ScoreResultDTO result = strategy.calculateScore(conditions);
        result.setSafetyLevel(safetyLevel);
        result.setIsSafe(true);
        return result;
    }
}
