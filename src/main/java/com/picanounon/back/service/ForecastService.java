package com.picanounon.back.service;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.picanounon.back.dto.MarineWeatherDTO;
import com.picanounon.back.dto.response.DayForecastResponse;
import com.picanounon.back.dto.response.HourlyForecastResponse;
import com.picanounon.back.dto.response.TideResponse;
import com.picanounon.back.dto.scoring.MarineConditionsDTO;
import com.picanounon.back.dto.scoring.ScoreResultDTO;
import com.picanounon.back.model.Port;
import com.picanounon.back.model.Species;
import com.picanounon.back.model.TidePhase;
import com.picanounon.back.model.TideType;
import com.picanounon.back.repository.PortRepository;
import com.picanounon.back.service.scoring.ScoringService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class ForecastService {

    private final PortRepository portRepository;
    private final MarineWeatherService marineWeatherService;
    private final TideService tideService;
    private final ScoringService scoringService;

    @Cacheable(value = "forecast", key = "{#portId, #date != null ? #date.toString() : T(java.time.LocalDate).now().toString(), #species != null ? #species.name() : 'SARGOS'}")
    public DayForecastResponse getForecastForPort(Long portId, LocalDate date, Species species) {
        Optional<Port> portOpt = portRepository.findById(portId);
        if (portOpt.isEmpty()) {
            return null;
        }

        Port port = portOpt.get();
        LocalDate targetDate = date != null ? date : LocalDate.now();
        Species targetSpecies = species != null ? species : Species.SARGOS;
        String targetDateStr = targetDate.toString();

        MarineWeatherDTO weather = marineWeatherService.getForecast(port.getLat(), port.getLng());
        List<TideResponse> tides = tideService.getTidesForPortAndDateRange(port, targetDate.minusDays(1), targetDate.plusDays(1));

        // Calcular coeficientes de ciclo e diario
        List<Integer> dailyCycleCoeffs = new ArrayList<>();
        if (tides != null && !tides.isEmpty()) {
            List<TideResponse> dayTides = tides.stream()
                    .filter(t -> t.getTideDateTime().toLocalDate().equals(targetDate))
                    .sorted(Comparator.comparing(TideResponse::getTideDateTime))
                    .toList();
            for (int k = 0; k < dayTides.size() - 1; k++) {
                double amp = Math.abs(dayTides.get(k).getHeight() - dayTides.get(k + 1).getHeight());
                dailyCycleCoeffs.add(tideService.calcularCoeficiente(amp));
            }
        }
        Integer avgDailyCoefficient = dailyCycleCoeffs.isEmpty()
                ? null
                : (int) Math.round(dailyCycleCoeffs.stream().mapToInt(Integer::intValue).average().orElse(0));

        List<HourlyForecastResponse> hourlyResponses = new ArrayList<>();
        if (weather != null && weather.getTime() != null) {
            List<Integer> matchingIndices = new ArrayList<>();
            for (int i = 0; i < weather.getTime().size(); i++) {
                if (weather.getTime().get(i).startsWith(targetDateStr)) {
                    matchingIndices.add(i);
                }
            }

            List<Integer> indicesToProcess = !matchingIndices.isEmpty()
                    ? matchingIndices
                    : createDefaultIndices(Math.min(24, weather.getTime().size()));

            List<LocalDateTime> sunrises = parseDateTimes(weather.getSunrise());
            List<LocalDateTime> sunsets = parseDateTimes(weather.getSunset());

            for (int i : indicesToProcess) {
                String rawTime = weather.getTime().get(i);
                LocalDateTime dateTime = parseIsoDateTime(rawTime);

                // Determinar fase de marea, altura e coeficiente instantaneo do ciclo
                TidePhase tidePhase = TidePhase.ENCHENTE;
                double tideHeight = 2.0;
                boolean isTideRising = true;
                Integer tideCoefficient = avgDailyCoefficient;

                if (tides != null && !tides.isEmpty()) {
                    List<TideResponse> sortedTides = tides.stream()
                            .sorted(Comparator.comparing(TideResponse::getTideDateTime))
                            .toList();

                    int nextIdx = -1;
                    for (int t = 0; t < sortedTides.size(); t++) {
                        if (sortedTides.get(t).getTideDateTime().isAfter(dateTime)) {
                            nextIdx = t;
                            break;
                        }
                    }

                    TideResponse prevTide;
                    TideResponse nextTide;

                    if (nextIdx > 0) {
                        prevTide = sortedTides.get(nextIdx - 1);
                        nextTide = sortedTides.get(nextIdx);
                    } else if (nextIdx == 0) {
                        prevTide = sortedTides.get(0);
                        nextTide = sortedTides.size() > 1 ? sortedTides.get(1) : sortedTides.get(0);
                    } else {
                        prevTide = sortedTides.size() > 1 ? sortedTides.get(sortedTides.size() - 2) : sortedTides.get(sortedTides.size() - 1);
                        nextTide = sortedTides.get(sortedTides.size() - 1);
                    }

                    isTideRising = nextTide.getHeight() > prevTide.getHeight();
                    tideHeight = tideService.calcularAlturaMareaActual(prevTide, nextTide, dateTime);

                    double amplitude = Math.abs(nextTide.getHeight() - prevTide.getHeight());
                    tideCoefficient = tideService.calcularCoeficiente(amplitude);

                    long diffPrevMinutes = Math.abs(Duration.between(prevTide.getTideDateTime(), dateTime).toMinutes());
                    long diffNextMinutes = Math.abs(Duration.between(nextTide.getTideDateTime(), dateTime).toMinutes());

                    if (diffPrevMinutes <= 45) {
                        tidePhase = prevTide.getType() == TideType.PLEAMAR ? TidePhase.PREAMAR : TidePhase.BAIXAMAR;
                    } else if (diffNextMinutes <= 45) {
                        tidePhase = nextTide.getType() == TideType.PLEAMAR ? TidePhase.PREAMAR : TidePhase.BAIXAMAR;
                    } else {
                        tidePhase = isTideRising ? TidePhase.ENCHENTE : TidePhase.MINGUANTE;
                    }
                } else {
                    int hour = dateTime.getHour();
                    isTideRising = (hour % 12) < 6;
                    tidePhase = isTideRising ? TidePhase.ENCHENTE : TidePhase.MINGUANTE;
                }

                // Calculo Crepuscular (+/- 60 min de amencer ou solpor)
                boolean isCrepuscular = false;
                if (!sunrises.isEmpty() || !sunsets.isEmpty()) {
                    boolean nearSunrise = sunrises.stream().anyMatch(s -> Math.abs(Duration.between(s, dateTime).toMinutes()) <= 60);
                    boolean nearSunset = sunsets.stream().anyMatch(s -> Math.abs(Duration.between(s, dateTime).toMinutes()) <= 60);
                    isCrepuscular = nearSunrise || nearSunset;
                } else {
                    int hour = dateTime.getHour();
                    isCrepuscular = (hour == 7 || hour == 8 || hour == 21 || hour == 22);
                }

                Double waveHeight = getValueOrDefault(weather.getWaveHeight(), i, 0.0);
                Double wavePeriod = getValueOrDefault(weather.getWavePeriod(), i, 9.0);
                Double windSpeed = getValueOrDefault(weather.getWindSpeed(), i, 0.0);
                Double windDirection = getValueOrDefault(weather.getWindDirection(), i, 0.0);
                Integer isDay = getValueOrDefault(weather.getIsDay(), i, 1);
                Double seaTemperature = getValueOrDefault(weather.getSeaTemperature(), i, 15.0);
                Double airTemperature = getValueOrDefault(weather.getTemperature(), i, 20.0);

                LocalDateTime closestLowTide = tides.stream()
                    .filter(t -> t.getType() == TideType.BAJAMAR)
                    .map(t -> t.getTideDateTime())
                    .min(Comparator.comparingLong(tideTime -> Math.abs(ChronoUnit.MINUTES.between(dateTime, tideTime))))
                    .orElse(null);

                    Integer minutesToLow = null;
                    if (closestLowTide != null) {
                        minutesToLow = (int) Math.abs(ChronoUnit.MINUTES.between(dateTime, closestLowTide));
                    }

                MarineConditionsDTO conditions = MarineConditionsDTO.builder()
                        .waveHeight(waveHeight)
                        .wavePeriod(wavePeriod)
                        .windSpeed(windSpeed)
                        .windDirection(windDirection)
                        .tidePhase(tidePhase)
                        .tideHeight(tideHeight)
                        .tideCoefficient(tideCoefficient)
                        .isCrepuscular(isCrepuscular)
                        .isDaylight(isDay == 1)
                        .waterTemperature(seaTemperature)
                        .temperature(airTemperature)
                        .minutesToLowTide(minutesToLow)
                        .build();

                ScoreResultDTO requestedResult = scoringService.calculateScore(conditions, targetSpecies);
                ScoreResultDTO sargoResult = scoringService.calculateScore(conditions, Species.SARGOS);
                ScoreResultDTO robalizaResult = scoringService.calculateScore(conditions, Species.ROBALIZA);
                ScoreResultDTO agullaResult = scoringService.calculateScore(conditions, Species.AGULLAS);
                ScoreResultDTO xardaResult = scoringService.calculateScore(conditions, Species.XARDA);

                String formattedTime = String.format("%02d:%02d", dateTime.getHour(), dateTime.getMinute());

                hourlyResponses.add(HourlyForecastResponse.builder()
                        .time(formattedTime)
                        .dateTime(rawTime)
                        .waveHeight(waveHeight)
                        .wavePeriod(wavePeriod)
                        .windSpeed(windSpeed)
                        .windDirection(windDirection)
                        .seaTemperature(seaTemperature)
                        .temperature(airTemperature)
                        .tideHeight(tideHeight)
                        .tideCoefficient(tideCoefficient)
                        .isTideRising(isTideRising)
                        .tidePhase(tidePhase)
                        .isSafe(requestedResult.getIsSafe())
                        .safetyLevel(requestedResult.getSafetyLevel())
                        .score(requestedResult.getScore())
                        .verdict(requestedResult.getVerdict())
                        .scoreSargos(sargoResult.getScore())
                        .scoreRobaliza(robalizaResult.getScore())
                        .scoreAgullas(agullaResult.getScore())
                        .scoreXardas(xardaResult.getScore())
                        .build());
            }
        }

        return DayForecastResponse.builder()
                .portId(port.getId())
                .portName(port.getName())
                .date(targetDateStr)
                .selectedSpecies(targetSpecies)
                .dailyCoefficient(avgDailyCoefficient)
                .cycleCoefficients(dailyCycleCoeffs)
                .hourlyForecasts(hourlyResponses)
                .build();
    }

    private List<Integer> createDefaultIndices(int length) {
        List<Integer> list = new ArrayList<>();
        for (int i = 0; i < length; i++) {
            list.add(i);
        }
        return list;
    }

    private LocalDateTime parseIsoDateTime(String raw) {
        try {
            return LocalDateTime.parse(raw);
        } catch (Exception e) {
            try {
                return LocalDateTime.parse(raw, DateTimeFormatter.ISO_DATE_TIME);
            } catch (Exception ex) {
                return LocalDate.parse(raw.substring(0, 10)).atStartOfDay();
            }
        }
    }

    private List<LocalDateTime> parseDateTimes(List<String> rawList) {
        if (rawList == null) return List.of();
        List<LocalDateTime> list = new ArrayList<>();
        for (String raw : rawList) {
            if (raw != null && !raw.isBlank()) {
                try {
                    list.add(parseIsoDateTime(raw));
                } catch (Exception ignored) {}
            }
        }
        return list;
    }

    private <T> T getValueOrDefault(List<T> list, int index, T defaultValue) {
        if (list != null && index >= 0 && index < list.size() && list.get(index) != null) {
            return list.get(index);
        }
        return defaultValue;
    }
}