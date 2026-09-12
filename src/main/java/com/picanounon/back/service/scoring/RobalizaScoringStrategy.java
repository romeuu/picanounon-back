package com.picanounon.back.service.scoring;

import com.picanounon.back.dto.scoring.MarineConditionsDTO;
import com.picanounon.back.dto.scoring.ScoreResultDTO;
import com.picanounon.back.model.SafetyLevel;
import com.picanounon.back.model.Species;
import com.picanounon.back.model.TidePhase;
import org.springframework.stereotype.Component;

@Component
public class RobalizaScoringStrategy implements SpeciesScoringStrategy {

    @Override
    public Species getSpecies() {
        return Species.ROBALIZA;
    }

    @Override
    public ScoreResultDTO calculateScore(MarineConditionsDTO conditions) {
        double waveHeight = conditions.getWaveHeight() != null ? conditions.getWaveHeight() : 0.0;
        double windSpeed = conditions.getWindSpeed() != null ? conditions.getWindSpeed() : 0.0;
        TidePhase tidePhase = conditions.getTidePhase() != null ? conditions.getTidePhase() : TidePhase.ENCHENTE;
        boolean isCrepuscular = Boolean.TRUE.equals(conditions.getIsCrepuscular());
        boolean isDaylight = Boolean.TRUE.equals(conditions.getIsDaylight());
        Double waterTemperature = conditions.getWaterTemperature();

        // 1. Mar (35 pts)
        int waveScore;
        if (waveHeight >= 1.2 && waveHeight <= 1.6) {
            waveScore = 35;
        } else if (waveHeight >= 0.9 && waveHeight < 1.2) {
            waveScore = 25;
        } else if (waveHeight > 1.6 && waveHeight <= 2.0) {
            waveScore = 18;
        } else if (waveHeight >= 0.6 && waveHeight < 0.9) {
            waveScore = 10;
        } else {
            waveScore = 4;
        }

        // 2. Luz (20 pts)
        int lightScore;
        if (isCrepuscular) {
            lightScore = 20;
        } else if (!isDaylight) {
            lightScore = 12;
        } else {
            lightScore = 4;
        }

        // 3. Fase de Marea (15 pts)
        int tideScore;
        if (tidePhase == TidePhase.ENCHENTE || tidePhase == TidePhase.MINGUANTE) {
            tideScore = 15;
        } else if (tidePhase == TidePhase.PREAMAR) {
            tideScore = 8;
        } else {
            tideScore = 0;
        }

        // 4. Temp. Auga (15 pts)
        int waterTempScore;
        if (waterTemperature != null) {
            if (waterTemperature >= 13.0 && waterTemperature <= 18.0) {
                waterTempScore = 15;
            } else if (waterTemperature >= 11.0 && waterTemperature < 13.0) {
                waterTempScore = 8;
            } else if (waterTemperature > 18.0 && waterTemperature <= 20.0) {
                waterTempScore = 10;
            } else {
                waterTempScore = 2;
            }
        } else {
            waterTempScore = 10;
        }

        // 5. Vento (10 pts)
        int windScore;
        if (windSpeed >= 8.0 && windSpeed <= 20.0) {
            windScore = 10;
        } else if (windSpeed < 8.0) {
            windScore = 7;
        } else if (windSpeed > 20.0 && windSpeed <= 25.0) {
            windScore = 3;
        } else {
            windScore = 0;
        }

        // 6. Modificador por coeficiente de marea (max +5 pts)
        // Morta (<45): -5, Media (45-65): +2, Viva Boa (65-85): +5, Viva Extrema (>85): +5
        int coeffModifier = 0;
        if (conditions.getTideCoefficient() != null) {
            int coef = conditions.getTideCoefficient();
            if (coef < 45) {
                coeffModifier = -5;
            } else if (coef < 65) {
                coeffModifier = 2;
            } else {
                coeffModifier = 5;
            }
        }

        int finalScore = Math.max(0, Math.min(100, waveScore + lightScore + tideScore + waterTempScore + windScore + coeffModifier));

        String verdict = "Condicións desfavorables";
        if (finalScore >= 80) {
            verdict = "Condicións óptimas (Moi bo momento)";
        } else if (finalScore >= 60) {
            verdict = "Condicións favorables (Actividade boa)";
        } else if (finalScore >= 40) {
            verdict = "Condicións regulares (Actividade moderada)";
        }

        return ScoreResultDTO.builder()
                .score(finalScore)
                .isSafe(true)
                .safetyLevel(SafetyLevel.SEGURO)
                .verdict(verdict)
                .build();
    }
}