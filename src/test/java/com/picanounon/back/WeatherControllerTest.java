package com.picanounon.back;

import com.picanounon.back.dto.MarineWeatherDTO;
import com.picanounon.back.service.MarineWeatherService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class WeatherControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private MarineWeatherService marineWeatherService;

    @Test
    void shouldReturnWeatherForecastWrappedInApiResponse() throws Exception {
        MarineWeatherDTO mockDto = MarineWeatherDTO.builder()
                .time(List.of("2026-09-05T00:00", "2026-09-05T01:00"))
                .waveHeight(List.of(1.42, 1.38))
                .wavePeriod(List.of(9.8, 9.6))
                .windSpeed(List.of(14.2, 12.8))
                .isDay(List.of(0, 0))
                .seaTemperature(List.of(16.4, 16.4))
                .temperature(List.of(17.5, 17.1))
                .sunrise(List.of("2026-09-05T07:54"))
                .sunset(List.of("2026-09-05T21:12"))
                .build();

        when(marineWeatherService.getForecast(42.88, -8.95)).thenReturn(mockDto);

        mockMvc.perform(get("/api/weather/forecast?lat=42.88&lng=-8.95"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.time[0]").value("2026-09-05T00:00"))
                .andExpect(jsonPath("$.data.waveHeight[0]").value(1.42))
                .andExpect(jsonPath("$.data.windSpeed[0]").value(14.2))
                .andExpect(jsonPath("$.data.sunrise[0]").value("2026-09-05T07:54"));
    }
}
