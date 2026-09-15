package com.picanounon.back.service.scoring;

import org.springframework.stereotype.Component;

import com.picanounon.back.dto.scoring.MarineConditionsDTO;
import com.picanounon.back.dto.scoring.ScoreResultDTO;
import com.picanounon.back.model.SafetyLevel;
import com.picanounon.back.model.Species;
import com.picanounon.back.model.TidePhase;

@Component
public class XardaScoringStrategy implements SpeciesScoringStrategy {

    @Override
    public Species getSpecies() {
        return Species.XARDA;
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
                    .verdict("Sen actividade nocturna para pesca a boia.")
                    .build();
        }

        double waveHeight = conditions.getWaveHeight() != null ? conditions.getWaveHeight() : 0.0;
        double windSpeed = conditions.getWindSpeed() != null ? conditions.getWindSpeed() : 0.0;
        TidePhase tidePhase = conditions.getTidePhase() != null ? conditions.getTidePhase() : TidePhase.ENCHENTE;
        Double waterTemperature = conditions.getWaterTemperature();

        // 1. Mar (35 pts)
        int waveScore;
        if (waveHeight <= 0.7) {
            waveScore = 35;
        } else if (waveHeight <= 1.1) {
            waveScore = 22;
        } else if (waveHeight <= 1.5) {
            waveScore = 10;
        } else {
            waveScore = 0;
        }

        // 2. Luz (25 pts)
        int lightScore;
        if (isDaylight && !isCrepuscular) {
            lightScore = 25;
        } else if (isCrepuscular) {
            lightScore = 15;
        } else {
            lightScore = 0;
        }

        // 3. Temp. Auga (20 pts)
        int waterTempScore;
        if (waterTemperature != null) {
            if (waterTemperature >= 16.0) {
                waterTempScore = 20;
            } else if (waterTemperature >= 14.0) {
                waterTempScore = 12;
            } else {
                waterTempScore = 2;
            }
        } else {
            waterTempScore = 14;
        }

        // 4. Vento (10 pts)
        int windScore;
        if (windSpeed <= 15.0) {
            windScore = 10;
        } else if (windSpeed <= 22.0) {
            windScore = 5;
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
        // Morta (<45): -2, Media (45-65): +4 (optimo macizo), Viva Boa (65-85): +2, Viva 85-90: 0, Viva Extrema (>90): -5 (lava o macizo)
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
                coeffModifier = -5;
            }
        }

        int finalScore = Math.max(0, Math.min(100, waveScore + lightScore + waterTempScore + windScore + tideScore + coeffModifier));

        // En caso de que a marea este no punto máis baixo, limitamos o score a como máximo a un aprobado limitado (55-60)
        if (tidePhase == TidePhase.BAIXAMAR) {
            int scoreReducido = (int) Math.round(finalScore * 0.65);
            finalScore = Math.min(scoreReducido, 58);
        }

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