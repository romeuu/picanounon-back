package com.picanounon.back.service.scoring;

import com.picanounon.back.dto.scoring.MarineConditionsDTO;
import com.picanounon.back.dto.scoring.ScoreResultDTO;
import com.picanounon.back.model.SafetyLevel;
import com.picanounon.back.model.Species;
import com.picanounon.back.model.TidePhase;
import org.springframework.stereotype.Component;

@Component
public class SargoScoringStrategy implements SpeciesScoringStrategy {

    @Override
    public Species getSpecies() {
        return Species.SARGOS;
    }

    @Override
    public ScoreResultDTO calculateScore(MarineConditionsDTO conditions) {
        double waveHeight = conditions.getWaveHeight() != null ? conditions.getWaveHeight() : 0.0;
        double windSpeed = conditions.getWindSpeed() != null ? conditions.getWindSpeed() : 0.0;
        TidePhase tidePhase = conditions.getTidePhase() != null ? conditions.getTidePhase() : TidePhase.ENCHENTE;
        Double waterTemperature = conditions.getWaterTemperature();

        // 1. Mar (45 pts)
        int waveScore;
        if (waveHeight >= 1.3 && waveHeight <= 1.8) {
            waveScore = 45;
        } else if (waveHeight >= 1.0 && waveHeight < 1.3) {
            waveScore = 32;
        } else if (waveHeight > 1.8 && waveHeight <= 2.1) {
            waveScore = 25;
        } else if (waveHeight >= 0.7 && waveHeight < 1.0) {
            waveScore = 12;
        } else {
            waveScore = 0;
        }

        // 2. Fase de Marea (20 pts)
        int tideScore;
        if (tidePhase == TidePhase.ENCHENTE) {
            tideScore = 20;
        } else if (tidePhase == TidePhase.PREAMAR) {
            tideScore = 16;
        } else if (tidePhase == TidePhase.MINGUANTE) {
            tideScore = 6;
        } else {
            tideScore = 0;
        }

        // 3. Temp. Auga (15 pts)
        int waterTempScore;
        if (waterTemperature != null) {
            if (waterTemperature >= 13.0 && waterTemperature <= 17.0) {
                waterTempScore = 15;
            } else if (waterTemperature >= 11.0 && waterTemperature < 13.0) {
                waterTempScore = 7;
            } else if (waterTemperature > 17.0 && waterTemperature <= 20.0) {
                waterTempScore = 4;
            } else {
                waterTempScore = 0;
            }
        } else {
            waterTempScore = 10;
        }

        // 4. Vento (15 pts)
        int windScore;
        if (windSpeed >= 10.0 && windSpeed <= 22.0) {
            windScore = 15;
        } else if (windSpeed < 10.0) {
            windScore = 9;
        } else if (windSpeed <= 28.0) {
            windScore = 4;
        } else {
            windScore = 0;
        }

        // 5. Modificador por coeficiente de marea (max +5 pts)
        // Morta (<45): -5, Media (45-65): +1, Viva Boa (65-85): +5, Viva (85-90): +3, Viva Extrema (>90): +1
        int coeffModifier = 0;
        if (conditions.getTideCoefficient() != null) {
            int coef = conditions.getTideCoefficient();
            if (coef < 45) {
                coeffModifier = -5;
            } else if (coef < 65) {
                coeffModifier = 1;
            } else if (coef <= 85) {
                coeffModifier = 5;
            } else if (coef <= 90) {
                coeffModifier = 3;
            } else {
                coeffModifier = 1;
            }
        }

        int finalScore = Math.max(0, Math.min(100, waveScore + tideScore + waterTempScore + windScore + coeffModifier));

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