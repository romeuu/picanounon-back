package com.picanounon.back;

import com.picanounon.back.client.openmeteo.OpenMeteoClient;
import com.picanounon.back.dto.MarineWeatherDTO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;
import static org.hamcrest.Matchers.containsString;

@RestClientTest(OpenMeteoClient.class)
class OpenMeteoClientTest {

    @Autowired
    private OpenMeteoClient openMeteoClient;

    @Autowired
    private MockRestServiceServer server;

    @Test
    void shouldFetchAndCombineMarineAndWeatherForecasts() {
        String marineJson = """
            {
              "hourly": {
                "time": ["2026-09-05T00:00", "2026-09-05T01:00"],
                "wave_height": [1.42, 1.38],
                "wave_period": [9.8, 9.6],
                "sea_surface_temperature": [16.4, 16.4]
              }
            }
            """;

        String weatherJson = """
            {
              "hourly": {
                "time": ["2026-09-05T00:00", "2026-09-05T01:00"],
                "wind_speed_10m": [14.2, 12.8],
                "is_day": [0, 0],
                "temperature_2m": [17.5, 17.1]
              },
              "daily": {
                "time": ["2026-09-05"],
                "sunrise": ["2026-09-05T07:54"],
                "sunset": ["2026-09-05T21:12"]
              }
            }
            """;

        server.expect(requestTo(containsString("marine-api.open-meteo.com/v1/marine")))
                .andRespond(withSuccess(marineJson, MediaType.APPLICATION_JSON));

        server.expect(requestTo(containsString("api.open-meteo.com/v1/forecast")))
                .andRespond(withSuccess(weatherJson, MediaType.APPLICATION_JSON));

        MarineWeatherDTO result = openMeteoClient.fetchCombinedForecast(42.88, -8.95);

        assertNotNull(result);
        assertEquals(List.of("2026-09-05T00:00", "2026-09-05T01:00"), result.getTime());
        assertEquals(List.of(1.42, 1.38), result.getWaveHeight());
        assertEquals(List.of(9.8, 9.6), result.getWavePeriod());
        assertEquals(List.of(16.4, 16.4), result.getSeaTemperature());
        assertEquals(List.of(14.2, 12.8), result.getWindSpeed());
        assertEquals(List.of(0, 0), result.getIsDay());
        assertEquals(List.of(17.5, 17.1), result.getTemperature());
        assertEquals(List.of("2026-09-05T07:54"), result.getSunrise());
        assertEquals(List.of("2026-09-05T21:12"), result.getSunset());

        server.verify();
    }
}
