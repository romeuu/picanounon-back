package com.picanounon.back.service.scoring;

import com.picanounon.back.model.SafetyLevel;
import org.springframework.stereotype.Component;

@Component
public class SafetyEvaluator {

    /**
     * Densidade de fluxo de enerxia da onda: P approx 0.49 * Hs^2 * Tp (kW/m)
     */
    public double calculateWaveEnergy(double hs, double tp) {
        double energy = 0.49 * Math.pow(hs, 2) * tp;
        return Math.round(energy * 10.0) / 10.0;
    }

    public SafetyLevel evaluateSafety(double hs, double tp) {
        double energy = calculateWaveEnergy(hs, tp);
        // Vagas con periodo longo (>13s) aumentan exponencialmente o risco na costa
        if (energy > 28.0 || hs >= 2.2 || (hs >= 1.5 && tp >= 14.0)) {
            return SafetyLevel.PERIGO_EXTREMO;
        }
        if (energy > 16.0 || hs >= 1.7 || (hs >= 1.2 && tp >= 12.0)) {
            return SafetyLevel.PRECAUCION;
        }
        return SafetyLevel.SEGURO;
    }
}
