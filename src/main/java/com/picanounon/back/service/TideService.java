package com.picanounon.back.service;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.Normalizer;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.picanounon.back.dto.response.TideDayResponse;
import com.picanounon.back.dto.response.TideResponse;
import com.picanounon.back.mapper.TideMapper;
import com.picanounon.back.model.Port;
import com.picanounon.back.model.Tide;
import com.picanounon.back.repository.PortRepository;
import com.picanounon.back.repository.TideRepository;
import com.picanounon.back.util.MeteogaliciaCsvParser;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class TideService {

    private static final List<String> REFERENCE_STATIONS = List.of(
            "A Coruña",
            "Vigo",
            "Vilagarcía",
            "A Guarda",
            "Ferrol Porto Exterior"
    );

    private final TideRepository tideRepository;
    private final PortRepository portRepository;
    private final MeteogaliciaCsvParser csvParser;
    private final TideMapper tideMapper;

    private static final double AMPLITUDE_MINIMA = 1.15; // Corresponde a coef ~20
    private static final double AMPLITUDE_MAXIMA = 4.25; // Corresponde a coef ~120

    public int importFromInputStream(InputStream inputStream, String filename) throws Exception {
        String stationName = extractStationFromFilename(filename);
        List<Tide> parsedTides = csvParser.parse(inputStream, stationName);
        return saveTides(parsedTides);
    }

    public int importFromMultipartFile(MultipartFile file) throws Exception {
        return importFromInputStream(file.getInputStream(), file.getOriginalFilename());
    }

    public Map<String, Integer> importFromDirectory(Path directoryPath) {
        Map<String, Integer> results = new HashMap<>();
        if (!Files.exists(directoryPath) || !Files.isDirectory(directoryPath)) {
            return results;
        }

        try (Stream<Path> paths = Files.walk(directoryPath)) {
            List<Path> csvFiles = paths
                    .filter(Files::isRegularFile)
                    .filter(p -> p.toString().toLowerCase().endsWith(".csv"))
                    .collect(Collectors.toList());

            for (Path csvFile : csvFiles) {
                try (InputStream is = Files.newInputStream(csvFile)) {
                    int count = importFromInputStream(is, csvFile.getFileName().toString());
                    results.put(csvFile.getFileName().toString(), count);
                } catch (Exception e) {
                    log.error("Error importing CSV file {}: {}", csvFile.getFileName(), e.getMessage());
                    results.put(csvFile.getFileName().toString(), -1);
                }
            }
        } catch (Exception e) {
            log.error("Error reading directory {}: {}", directoryPath, e.getMessage());
        }
        return results;
    }

    @Cacheable(value = "tides", key = "{#portId, #date != null ? #date.toString() : T(java.time.LocalDate).now().toString()}")
    public TideDayResponse getTidesForPort(Long portId, LocalDate date) {
        LocalDate targetDate = date != null ? date : LocalDate.now();
        Optional<Port> portOpt = portRepository.findById(portId);
        if (portOpt.isEmpty()) {
            return null;
        }

        Port port = portOpt.get();
        String station = port.getTideStation();
        int offset = port.getTideOffsetMinutes() != null ? port.getTideOffsetMinutes() : 0;

        if (station == null || station.isBlank()) {
            return null;
        }

        String targetNormalizedStation = normalizeText(station);
        List<Tide> dateTides = tideRepository.findByTideDateOrderByTideTimeAsc(targetDate);

        List<Tide> portTides = dateTides.stream()
                .filter(t -> normalizeText(t.getStationName()).equals(targetNormalizedStation))
                .collect(Collectors.toList());

        List<Integer> coefficients = this.calculateCoeficientTides(portTides);

        Integer avgCoefficient = coefficients.isEmpty() ? null : (int) Math.round(coefficients.stream().mapToInt(Integer::intValue).average().orElse(0));

        List<TideResponse> tideResponses = portTides.stream()
            .map(tideMapper::toDTO)
            .map(dto -> tideMapper.toResponse(dto, port.getName(), offset))
            .collect(Collectors.toList());

        return TideDayResponse.builder()
                .tides(tideResponses)
                .dailyCoefficient(avgCoefficient)
                .cycleCoefficient(coefficients)
                .build();
    }

    public List<TideResponse> getTidesForPortAndDateRange(Port port, LocalDate startDate, LocalDate endDate) {
        if (port == null) return List.of();
        String station = port.getTideStation();
        int offset = port.getTideOffsetMinutes() != null ? port.getTideOffsetMinutes() : 0;

        if (station == null || station.isBlank()) {
            return List.of();
        }

        String targetNormalizedStation = normalizeText(station);
        List<Tide> rangeTides = tideRepository.findByTideDateBetweenOrderByTideDateAscTideTimeAsc(startDate, endDate);

        return rangeTides.stream()
                .filter(t -> normalizeText(t.getStationName()).equals(targetNormalizedStation))
                .map(tideMapper::toDTO)
                .map(dto -> tideMapper.toResponse(dto, port.getName(), offset))
                .sorted((a, b) -> a.getTideDateTime().compareTo(b.getTideDateTime()))
                .collect(Collectors.toList());
    }

    public double calcularAlturaMareaActual(TideResponse anterior, TideResponse seguinte, java.time.LocalDateTime agora) {
        if (anterior == null && seguinte == null) return 2.0;
        if (anterior == null) return seguinte.getHeight();
        if (seguinte == null) return anterior.getHeight();

        long tInicio = anterior.getTideDateTime().atZone(java.time.ZoneId.systemDefault()).toInstant().toEpochMilli();
        long tFin = seguinte.getTideDateTime().atZone(java.time.ZoneId.systemDefault()).toInstant().toEpochMilli();
        long tAgora = agora.atZone(java.time.ZoneId.systemDefault()).toInstant().toEpochMilli();

        long duracionTotal = tFin - tInicio;
        if (duracionTotal <= 0) return anterior.getHeight();

        double tempoTranscorrido = tAgora - tInicio;
        double progreso = Math.min(Math.max((double) tempoTranscorrido / duracionTotal, 0.0), 1.0);

        // Curva cosenoidal: varía de 0 a 1 suavemente
        double factorCoseno = (1.0 - Math.cos(progreso * Math.PI)) / 2.0;

        double altura = anterior.getHeight() + (seguinte.getHeight() - anterior.getHeight()) * factorCoseno;
        return Math.round(altura * 100.0) / 100.0;
    }

    /**
     * Calcula o coeficiente de marea (escala 20 - 120) a partir da amplitude.
     *
     * @param alturaPreamar  Altura máxima en metros (ex: 3.3)
     * @param alturaBaixamar Altura mínima en metros (ex: 1.0)
     * @return Coeficiente enteiro calibrado
     */
    public int calcularCoeficiente(double amplitude) {
        // Interpolación lineal sobre o rango 20 - 120
        double ratio = (amplitude - AMPLITUDE_MINIMA) / (AMPLITUDE_MAXIMA - AMPLITUDE_MINIMA);
        double coefCalculado = 20.0 + (ratio * 100.0);

        // Axustamos para que non se saia dos límites teóricos habituais
        int coefFinal = (int) Math.round(coefCalculado);
        return Math.max(20, Math.min(120, coefFinal));
    }
    

    public List<Integer> calculateCoeficientTides(List<Tide> mareas) {
        List<Integer> coeficientes = new ArrayList<>();

        for (int i = 0; i < mareas.size() - 1; i++) {
            Tide actual = mareas.get(i);
            Tide siguiente = mareas.get(i + 1);

            if (!actual.getType().equals(siguiente.getType())) {
                double amplitude = Math.abs(actual.getHeight() - siguiente.getHeight());
                coeficientes.add(calcularCoeficiente(amplitude));
            }
        }

        return coeficientes;
    }

    public Map<String, Long> getStationStatistics() {
        List<Tide> allTides = tideRepository.findAll();
        Map<String, Long> stats = new HashMap<>();
        for (Tide tide : allTides) {
            stats.merge(tide.getStationName(), 1L, Long::sum);
        }
        return stats;
    }

    private int saveTides(List<Tide> tides) {
        int savedCount = 0;
        for (Tide tide : tides) {
            Optional<Tide> existing = tideRepository.findByStationNameAndTideDateAndTideTime(
                    tide.getStationName(),
                    tide.getTideDate(),
                    tide.getTideTime()
            );

            if (existing.isPresent()) {
                Tide t = existing.get();
                t.setType(tide.getType());
                t.setHeight(tide.getHeight());
                tideRepository.save(t);
            } else {
                tideRepository.save(tide);
            }
            savedCount++;
        }
        return savedCount;
    }

    public String extractStationFromFilename(String filename) {
        if (filename == null || filename.isBlank()) {
            return "Desconocida";
        }
        String normalizedFilename = normalizeText(filename);

        for (String station : REFERENCE_STATIONS) {
            String normalizedStation = normalizeText(station);
            if (normalizedFilename.contains(normalizedStation)) {
                return station;
            }
        }

        if (normalizedFilename.contains("coruna")) return "A Coruña";
        if (normalizedFilename.contains("vilagarcia")) return "Vilagarcía";
        if (normalizedFilename.contains("guarda")) return "A Guarda";
        if (normalizedFilename.contains("exterior") || normalizedFilename.contains("ferrol")) return "Ferrol Porto Exterior";
        if (normalizedFilename.contains("vigo")) return "Vigo";

        String clean = filename.replace(".csv", "").replace(".CSV", "");
        clean = clean.replaceAll("(?i)^mareas[_-]?", "").replaceAll("[_-]?\\d{4}$", "").trim();
        return clean.isEmpty() ? "Desconocida" : clean;
    }

    private String normalizeText(String text) {
        if (text == null) return "";
        String nfdNormalizedString = Normalizer.normalize(text, Normalizer.Form.NFD);
        return nfdNormalizedString.replaceAll("\\p{InCombiningDiacriticalMarks}+", "")
                .toLowerCase()
                .replaceAll("[^a-z0-9]", "");
    }
}
