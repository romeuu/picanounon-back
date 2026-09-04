package com.picanounon.back.controller;

import com.picanounon.back.dto.response.ApiResponse;
import com.picanounon.back.dto.response.TideResponse;
import com.picanounon.back.service.TideDirectoryWatcher;
import com.picanounon.back.service.TideService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/tides")
@RequiredArgsConstructor
public class TideController {

    private final TideService tideService;
    private final TideDirectoryWatcher directoryWatcher;

    @PostMapping("/sync-folder")
    public ResponseEntity<ApiResponse<Map<String, Integer>>> syncFolder() {
        Map<String, Integer> result = directoryWatcher.syncDirectory();
        return ResponseEntity.ok(ApiResponse.success("Directory synchronization completed", result));
    }

    @PostMapping("/upload")
    public ResponseEntity<ApiResponse<Map<String, Integer>>> uploadFiles(@RequestParam("files") MultipartFile[] files) {
        Map<String, Integer> results = new HashMap<>();
        for (MultipartFile file : files) {
            try {
                int importedCount = tideService.importFromMultipartFile(file);
                results.put(file.getOriginalFilename(), importedCount);
            } catch (Exception e) {
                results.put(file.getOriginalFilename(), -1);
            }
        }
        return ResponseEntity.ok(ApiResponse.success("Batch CSV upload processed", results));
    }

    @GetMapping("/port/{portId}")
    public ResponseEntity<ApiResponse<List<TideResponse>>> getTidesByPort(
            @PathVariable Long portId,
            @RequestParam(value = "date", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        
        LocalDate targetDate = date != null ? date : LocalDate.now();
        List<TideResponse> tides = tideService.getTidesForPort(portId, targetDate);
        return ResponseEntity.ok(ApiResponse.success(tides));
    }

    @GetMapping("/stations")
    public ResponseEntity<ApiResponse<Map<String, Long>>> getStationStats() {
        Map<String, Long> stats = tideService.getStationStatistics();
        return ResponseEntity.ok(ApiResponse.success(stats));
    }
}
