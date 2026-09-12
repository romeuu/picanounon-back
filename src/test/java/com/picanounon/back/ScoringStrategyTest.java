package com.picanounon.back;

import com.picanounon.back.dto.scoring.MarineConditionsDTO;
import com.picanounon.back.dto.scoring.ScoreResultDTO;
import com.picanounon.back.model.SafetyLevel;
import com.picanounon.back.model.Species;
import com.picanounon.back.model.TidePhase;
import com.picanounon.back.service.scoring.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ScoringStrategyTest {

    private SafetyEvaluator safetyEvaluator;
    private SargoScoringStrategy sargoStrategy;
    private RobalizaScoringStrategy robalizaStrategy;
    private AgullaScoringStrategy agullaStrategy;
    private XardaScoringStrategy xardaStrategy;
    private ScoringService scoringService;

    @BeforeEach
    void setUp() {
        safetyEvaluator = new SafetyEvaluator();
        sargoStrategy = new SargoScoringStrategy();
        robalizaStrategy = new RobalizaScoringStrategy();
        agullaStrategy = new AgullaScoringStrategy();
        xardaStrategy = new XardaScoringStrategy();
        scoringService = new ScoringService(safetyEvaluator, List.of(sargoStrategy, robalizaStrategy, agullaStrategy, xardaStrategy));
    }

    @Test
    void testSafetyEvaluation_ExtremeDanger() {
        SafetyLevel level = safetyEvaluator.evaluateSafety(2.5, 10.0);
        assertEquals(SafetyLevel.PERIGO_EXTREMO, level);

        MarineConditionsDTO conditions = MarineConditionsDTO.builder()
                .waveHeight(2.5)
                .wavePeriod(10.0)
                .windSpeed(15.0)
                .tidePhase(TidePhase.ENCHENTE)
                .waterTemperature(15.0)
                .build();

        ScoreResultDTO result = scoringService.calculateScore(conditions, Species.SARGOS);
        assertFalse(result.getIsSafe());
        assertEquals(10, result.getScore());
        assertEquals(SafetyLevel.PERIGO_EXTREMO, result.getSafetyLevel());
    }

    @Test
    void testSargoScoring_TideCoefficients() {
        // Optimal: wave 1.5 (45) + tide ENCHENTE (20) + waterTemp 15 (15) + wind 15 (15) + coef 75 (+5) = EXACTLY 100
        MarineConditionsDTO optConditions = MarineConditionsDTO.builder()
                .waveHeight(1.5)
                .wavePeriod(9.0)
                .windSpeed(15.0)
                .tidePhase(TidePhase.ENCHENTE)
                .waterTemperature(15.0)
                .tideCoefficient(75) // +5
                .build();

        ScoreResultDTO optResult = scoringService.calculateScore(optConditions, Species.SARGOS);
        assertEquals(100, optResult.getScore());

        // Dead tide coef 35 (-5): 45 + 20 + 15 + 15 - 5 = 90
        MarineConditionsDTO deadTideConditions = MarineConditionsDTO.builder()
                .waveHeight(1.5)
                .wavePeriod(9.0)
                .windSpeed(15.0)
                .tidePhase(TidePhase.ENCHENTE)
                .waterTemperature(15.0)
                .tideCoefficient(35) // -5
                .build();

        ScoreResultDTO deadResult = scoringService.calculateScore(deadTideConditions, Species.SARGOS);
        assertEquals(90, deadResult.getScore());
    }

    @Test
    void testRobalizaScoring_TideCoefficients() {
        // Optimal: wave 1.4 (35) + light crepuscular (20) + tide ENCHENTE (15) + waterTemp 15 (15) + wind 12 (10) + coef 75 (+5) = EXACTLY 100
        MarineConditionsDTO optConditions = MarineConditionsDTO.builder()
                .waveHeight(1.4)
                .wavePeriod(9.0)
                .windSpeed(12.0)
                .tidePhase(TidePhase.ENCHENTE)
                .waterTemperature(15.0)
                .isCrepuscular(true)
                .isDaylight(true)
                .tideCoefficient(75) // +5
                .build();

        ScoreResultDTO optResult = scoringService.calculateScore(optConditions, Species.ROBALIZA);
        assertEquals(100, optResult.getScore());

        // Dead tide (<45): 35 + 20 + 15 + 15 + 10 - 5 = 90
        MarineConditionsDTO deadConditions = MarineConditionsDTO.builder()
                .waveHeight(1.4)
                .wavePeriod(9.0)
                .windSpeed(12.0)
                .tidePhase(TidePhase.ENCHENTE)
                .waterTemperature(15.0)
                .isCrepuscular(true)
                .isDaylight(true)
                .tideCoefficient(30) // -5
                .build();

        ScoreResultDTO deadResult = scoringService.calculateScore(deadConditions, Species.ROBALIZA);
        assertEquals(90, deadResult.getScore());
    }

    @Test
    void testAgullaScoring_TideCoefficients() {
        // Full daylight: wave 0.4 (30) + light (25) + waterTemp 16 (15) + airTemp 19 (5) + wind 8 (15) + tide ENCHENTE (6) + coef 55 (+4) = EXACTLY 100
        MarineConditionsDTO optConditions = MarineConditionsDTO.builder()
                .waveHeight(0.4)
                .wavePeriod(8.0)
                .windSpeed(8.0)
                .tidePhase(TidePhase.ENCHENTE)
                .isDaylight(true)
                .isCrepuscular(false)
                .waterTemperature(16.0)
                .temperature(19.0)
                .tideCoefficient(55) // +4
                .build();

        ScoreResultDTO optResult = scoringService.calculateScore(optConditions, Species.AGULLAS);
        assertEquals(100, optResult.getScore());

        // Extreme tide (>90): 30 + 25 + 15 + 5 + 15 + 6 - 4 = 92
        MarineConditionsDTO extremeConditions = MarineConditionsDTO.builder()
                .waveHeight(0.4)
                .wavePeriod(8.0)
                .windSpeed(8.0)
                .tidePhase(TidePhase.ENCHENTE)
                .isDaylight(true)
                .isCrepuscular(false)
                .waterTemperature(16.0)
                .temperature(19.0)
                .tideCoefficient(100) // -4
                .build();

        ScoreResultDTO extremeResult = scoringService.calculateScore(extremeConditions, Species.AGULLAS);
        assertEquals(92, extremeResult.getScore());
    }

    @Test
    void testXardaScoring_TideCoefficients() {
        // Full daylight: wave 0.5 (35) + light (25) + waterTemp 17 (20) + wind 10 (10) + tide PREAMAR (6) + coef 55 (+4) = EXACTLY 100
        MarineConditionsDTO optConditions = MarineConditionsDTO.builder()
                .waveHeight(0.5)
                .wavePeriod(8.0)
                .windSpeed(10.0)
                .tidePhase(TidePhase.PREAMAR)
                .isDaylight(true)
                .isCrepuscular(false)
                .waterTemperature(17.0)
                .tideCoefficient(55) // +4
                .build();

        ScoreResultDTO optResult = scoringService.calculateScore(optConditions, Species.XARDA);
        assertEquals(100, optResult.getScore());

        // Extreme tide (>90): 35 + 25 + 20 + 10 + 6 - 5 = 91
        MarineConditionsDTO extremeConditions = MarineConditionsDTO.builder()
                .waveHeight(0.5)
                .wavePeriod(8.0)
                .windSpeed(10.0)
                .tidePhase(TidePhase.PREAMAR)
                .isDaylight(true)
                .isCrepuscular(false)
                .waterTemperature(17.0)
                .tideCoefficient(105) // -5
                .build();

        ScoreResultDTO extremeResult = scoringService.calculateScore(extremeConditions, Species.XARDA);
        assertEquals(91, extremeResult.getScore());
    }
}