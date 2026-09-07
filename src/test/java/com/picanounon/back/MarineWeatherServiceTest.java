package com.picanounon.back;

import com.picanounon.back.client.openmeteo.OpenMeteoClient;
import com.picanounon.back.dto.MarineWeatherDTO;
import com.picanounon.back.service.MarineWeatherService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

@SpringBootTest
class MarineWeatherServiceTest {

    @MockitoBean
    private OpenMeteoClient openMeteoClient;

    @Autowired
    private MarineWeatherService marineWeatherService;

    @Test
    void shouldCacheForecastAndOnlyCallClientOnce() {
        MarineWeatherDTO mockDto = MarineWeatherDTO.builder()
                .time(List.of("2026-09-05T00:00"))
                .waveHeight(List.of(1.5))
                .build();

        when(openMeteoClient.fetchCombinedForecast(42.8800, -8.9500)).thenReturn(mockDto);

        // First call - cache miss, calls client
        MarineWeatherDTO firstResult = marineWeatherService.getForecast(42.8800, -8.9500);
        assertNotNull(firstResult);
        assertEquals(List.of(1.5), firstResult.getWaveHeight());

        // Second call with same coordinates - cache hit, client NOT called again
        MarineWeatherDTO secondResult = marineWeatherService.getForecast(42.8800, -8.9500);
        assertNotNull(secondResult);
        assertEquals(List.of(1.5), secondResult.getWaveHeight());

        verify(openMeteoClient, times(1)).fetchCombinedForecast(42.8800, -8.9500);
    }
}
