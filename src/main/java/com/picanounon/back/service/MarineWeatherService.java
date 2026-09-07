package com.picanounon.back.service;

import com.picanounon.back.client.openmeteo.OpenMeteoClient;
import com.picanounon.back.dto.MarineWeatherDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class MarineWeatherService {

    private final OpenMeteoClient openMeteoClient;

    @Cacheable(value = "marineWeather", key = "T(java.lang.String).format(T(java.util.Locale).ROOT, '%.4f_%.4f', #lat, #lng)")
    public MarineWeatherDTO getForecast(double lat, double lng) {
        log.info("Cache miss for coordinates ({}, {}). Querying Open-Meteo...", lat, lng);
        return openMeteoClient.fetchCombinedForecast(lat, lng);
    }
}
