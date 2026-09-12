package com.picanounon.back.controller;

import com.picanounon.back.dto.response.ApiResponse;
import com.picanounon.back.dto.response.DayForecastResponse;
import com.picanounon.back.model.Species;
import com.picanounon.back.service.ForecastService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/forecast")
@RequiredArgsConstructor
public class ForecastController {

    private final ForecastService forecastService;

    @GetMapping("/port/{portId}")
    public ResponseEntity<ApiResponse<DayForecastResponse>> getForecast(
            @PathVariable Long portId,
            @RequestParam(value = "date", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam(value = "species", required = false) Species species) {

        DayForecastResponse forecast = forecastService.getForecastForPort(portId, date, species);
        if (forecast == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(ApiResponse.success(forecast));
    }
}
