package com.picanounon.back.controller;

import com.picanounon.back.dto.MarineWeatherDTO;
import com.picanounon.back.dto.response.ApiResponse;
import com.picanounon.back.service.MarineWeatherService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/weather")
@RequiredArgsConstructor
public class WeatherController {

    private final MarineWeatherService marineWeatherService;

    @GetMapping("/forecast")
    public ResponseEntity<ApiResponse<MarineWeatherDTO>> getForecast(
            @RequestParam("lat") double lat,
            @RequestParam("lng") double lng) {
        MarineWeatherDTO forecast = marineWeatherService.getForecast(lat, lng);
        return ResponseEntity.ok(ApiResponse.success(forecast));
    }
}
