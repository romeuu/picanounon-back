package com.picanounon.back.service;

import com.picanounon.back.dto.response.TideResponse;
import com.picanounon.back.mapper.TideMapper;
import com.picanounon.back.model.Port;
import com.picanounon.back.model.Tide;
import com.picanounon.back.repository.PortRepository;
import com.picanounon.back.repository.TideRepository;
import com.picanounon.back.util.MeteogaliciaCsvParser;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.Normalizer;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
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

    public TideService(TideRepository tideRepository,
                       PortRepository portRepository,
                       MeteogaliciaCsvParser csvParser,
                       TideMapper tideMapper) {
        this.tideRepository = tideRepository;
        this.portRepository = portRepository;
        this.csvParser = csvParser;
        this.tideMapper = tideMapper;
    }

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
                    results.put(csvFile.getFileName().toString(), -1);
                }
            }
        } catch (Exception e) {
            // Log error
        }
        return results;
    }

    public List<TideResponse> getTidesForPort(Long portId, LocalDate date) {
        LocalDate targetDate = date != null ? date : LocalDate.now();
        Optional<Port> portOpt = portRepository.findById(portId);
        if (portOpt.isEmpty()) {
            return List.of();
        }

        Port port = portOpt.get();
        String station = port.getTideStation();
        int offset = port.getTideOffsetMinutes() != null ? port.getTideOffsetMinutes() : 0;

        if (station == null || station.isBlank()) {
            return List.of();
        }

        String targetNormalizedStation = normalizeText(station);
        List<Tide> dateTides = tideRepository.findByTideDateOrderByTideTimeAsc(targetDate);

        List<Tide> portTides = dateTides.stream()
                .filter(t -> normalizeText(t.getStationName()).equals(targetNormalizedStation))
                .collect(Collectors.toList());

        return portTides.stream()
                .map(tideMapper::toDTO)
                .map(dto -> tideMapper.toResponse(dto, port.getName(), offset))
                .collect(Collectors.toList());
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
