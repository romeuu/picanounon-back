package com.picanounon.back.client.openmeteo;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import com.picanounon.back.client.openmeteo.dto.OpenMeteoMarineResponse;
import com.picanounon.back.client.openmeteo.dto.OpenMeteoWeatherResponse;
import com.picanounon.back.dto.MarineWeatherDTO;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class OpenMeteoClient {

    private final RestClient restClient;
    private final String marineUrl;
    private final String weatherUrl;

    public OpenMeteoClient(RestClient.Builder restClientBuilder,
                           @Value("${app.open-meteo.marine-url:https://marine-api.open-meteo.com/v1/marine}") String marineUrl,
                           @Value("${app.open-meteo.weather-url:https://api.open-meteo.com/v1/forecast}") String weatherUrl) {
        this.restClient = restClientBuilder.build();
        this.marineUrl = marineUrl;
        this.weatherUrl = weatherUrl;
    }

    public MarineWeatherDTO fetchCombinedForecast(double lat, double lng) {
        log.info("Fetching Open-Meteo external data for coordinates: lat={}, lng={}", lat, lng);

        OpenMeteoMarineResponse marineResponse = restClient.get()
                .uri(marineUrl + "?latitude={lat}&longitude={lng}&hourly=wave_height,wave_period,sea_surface_temperature&timezone=Europe/Madrid&forecast_days=7", lat, lng)
                .retrieve()
                .body(OpenMeteoMarineResponse.class);

        OpenMeteoWeatherResponse weatherResponse = restClient.get()
                .uri(weatherUrl + "?latitude={lat}&longitude={lng}&hourly=wind_speed_10m,wind_direction_10m,is_day,temperature_2m&daily=sunrise,sunset&timezone=Europe/Madrid&forecast_days=7", lat, lng)
                .retrieve()
                .body(OpenMeteoWeatherResponse.class);

        List<String> time = (marineResponse != null && marineResponse.getHourly() != null)
                ? marineResponse.getHourly().getTime()
                : (weatherResponse != null && weatherResponse.getHourly() != null ? weatherResponse.getHourly().getTime() : List.of());

        List<Double> waveHeight = (marineResponse != null && marineResponse.getHourly() != null && marineResponse.getHourly().getWaveHeight() != null)
                ? marineResponse.getHourly().getWaveHeight()
                : List.of();

        List<Double> wavePeriod = (marineResponse != null && marineResponse.getHourly() != null && marineResponse.getHourly().getWavePeriod() != null)
                ? marineResponse.getHourly().getWavePeriod()
                : List.of();

        List<Double> seaTemperature = (marineResponse != null && marineResponse.getHourly() != null && marineResponse.getHourly().getSeaSurfaceTemperature() != null)
                ? marineResponse.getHourly().getSeaSurfaceTemperature()
                : List.of();

        List<Double> windSpeed = (weatherResponse != null && weatherResponse.getHourly() != null && weatherResponse.getHourly().getWindSpeed10m() != null)
                ? weatherResponse.getHourly().getWindSpeed10m()
                : List.of();

        List<Double> windDirection = (weatherResponse != null && weatherResponse.getHourly() != null && weatherResponse.getHourly().getWindDirection10m() != null)
                ? weatherResponse.getHourly().getWindDirection10m()
                : List.of();

        List<Integer> isDay = (weatherResponse != null && weatherResponse.getHourly() != null && weatherResponse.getHourly().getIsDay() != null)
                ? weatherResponse.getHourly().getIsDay()
                : List.of();

        List<Double> temperature = (weatherResponse != null && weatherResponse.getHourly() != null && weatherResponse.getHourly().getTemperature2m() != null)
                ? weatherResponse.getHourly().getTemperature2m()
                : List.of();

        List<String> sunrise = (weatherResponse != null && weatherResponse.getDaily() != null && weatherResponse.getDaily().getSunrise() != null)
                ? weatherResponse.getDaily().getSunrise()
                : List.of();

        List<String> sunset = (weatherResponse != null && weatherResponse.getDaily() != null && weatherResponse.getDaily().getSunset() != null)
                ? weatherResponse.getDaily().getSunset()
                : List.of();

        return MarineWeatherDTO.builder()
                .time(time)
                .waveHeight(waveHeight)
                .wavePeriod(wavePeriod)
                .windSpeed(windSpeed)
                .windDirection(windDirection)
                .isDay(isDay)
                .seaTemperature(seaTemperature)
                .temperature(temperature)
                .sunrise(sunrise)
                .sunset(sunset)
                .build();
    }
}
