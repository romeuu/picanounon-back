package com.picanounon.back.service.scoring;

import org.springframework.stereotype.Component;

import com.picanounon.back.dto.scoring.MarineConditionsDTO;
import com.picanounon.back.dto.scoring.ScoreResultDTO;
import com.picanounon.back.model.SafetyLevel;
import com.picanounon.back.model.Species;
import com.picanounon.back.model.TidePhase;

@Component
public class AgullaScoringStrategy implements SpeciesScoringStrategy {

    @Override
    public Species getSpecies() {
        return Species.AGULLAS;
    }

    @Override
    public ScoreResultDTO calculateScore(MarineConditionsDTO conditions) {
        boolean isDaylight = Boolean.TRUE.equals(conditions.getIsDaylight());
        boolean isCrepuscular = Boolean.TRUE.equals(conditions.getIsCrepuscular());

        if (!isDaylight) {
            return ScoreResultDTO.builder()
                    .score(5)
                    .isSafe(true)
                    .safetyLevel(SafetyLevel.SEGURO)
                    .verdict("Sen actividade nocturna (Especie estritamente diurna).")
                    .build();
        }

        if (isCrepuscular) {
            return ScoreResultDTO.builder()
                    .score(20)
                    .isSafe(true)
                    .safetyLevel(SafetyLevel.SEGURO)
                    .verdict("Actividade moi baixa polo solpor/falta de sol.")
                    .build();
        }

        double waveHeight = conditions.getWaveHeight() != null ? conditions.getWaveHeight() : 0.0;
        double windSpeed = conditions.getWindSpeed() != null ? conditions.getWindSpeed() : 0.0;
        TidePhase tidePhase = conditions.getTidePhase() != null ? conditions.getTidePhase() : TidePhase.ENCHENTE;
        Double waterTemperature = conditions.getWaterTemperature();
        Double airTemperature = conditions.getTemperature();

        // 1. Mar (30 pts)
        int waveScore;
        if (waveHeight <= 0.6) {
            waveScore = 30;
        } else if (waveHeight <= 0.9) {
            waveScore = 20;
        } else if (waveHeight <= 1.2) {
            waveScore = 10;
        } else {
            waveScore = 0;
        }

        // 2. Luz (25 pts)
        int lightScore = 25;

        // 3. Temp. Auga e Aire (max 20 pts)
        int tempScore = 0;
        if (waterTemperature != null) {
            if (waterTemperature >= 16.0) {
                tempScore += 15;
            } else if (waterTemperature >= 14.0) {
                tempScore += 8;
            }
        } else {
            tempScore += 10;
        }

        if (airTemperature != null) {
            if (airTemperature >= 18.0) {
                tempScore += 5;
            }
        } else {
            tempScore += 3;
        }

        // 4. Vento (15 pts)
        int windScore;
        if (windSpeed < 10.0) {
            windScore = 15;
        } else if (windSpeed <= 18.0) {
            windScore = 8;
        } else {
            windScore = 0;
        }

        // 5. Fase de Marea (6 pts)
        int tideScore;
        if (tidePhase == TidePhase.PREAMAR || tidePhase == TidePhase.ENCHENTE) {
            tideScore = 6;
        } else if (tidePhase == TidePhase.MINGUANTE) {
            tideScore = 3; // A auga baixa pero aínda conserva calado
        } else {
            // BAIXAMAR: o peirao queda sen auga e a corrente para
            tideScore = 0;
        }

        // 6. Modificador por coeficiente de marea (max +4 pts)
        // Morta (<45): -2, Media (45-65 / 50-75): +4 (optimo), Viva Boa (65-85): +2, Viva 85-90: 0, Viva Extrema (>90): -4
        int coeffModifier = 0;
        if (conditions.getTideCoefficient() != null) {
            int coef = conditions.getTideCoefficient();
            if (coef < 45) {
                coeffModifier = -2;
            } else if (coef <= 65) {
                coeffModifier = 4;
            } else if (coef <= 85) {
                coeffModifier = 2;
            } else if (coef <= 90) {
                coeffModifier = 0;
            } else {
                coeffModifier = -4;
            }
        }

        int finalScore = Math.max(0, Math.min(100, waveScore + lightScore + tempScore + windScore + tideScore + coeffModifier));

        String verdict = "Condicións desfavorables";
        if (finalScore >= 80) {
            verdict = "Condicións óptimas (Moi bo momento)";
        } else if (finalScore >= 60) {
            verdict = "Condicións favorables (Actividade boa)";
        } else if (finalScore >= 40) {
            verdict = "Condicións regulares (Actividade moderada)";
        }

        // En caso de que a marea este no punto máis baixo, limitamos o score a 65, xa que limita pero a especie sigue saíndo a comer
        if (tidePhase == TidePhase.BAIXAMAR) {
            int scoreReducido = (int) Math.round(finalScore * 0.75);
            finalScore = Math.min(scoreReducido, 65);
        }

        return ScoreResultDTO.builder()
                .score(finalScore)
                .isSafe(true)
                .safetyLevel(SafetyLevel.SEGURO)
                .verdict(verdict)
                .build();
    }
}