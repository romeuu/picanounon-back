package com.picanounon.back;

import com.picanounon.back.dto.response.DayForecastResponse;
import com.picanounon.back.dto.response.HourlyForecastResponse;
import com.picanounon.back.model.Species;
import com.picanounon.back.service.ForecastService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class ForecastControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ForecastService forecastService;

    @Test
    void shouldReturnForecastForPort() throws Exception {
        HourlyForecastResponse hour = HourlyForecastResponse.builder()
                .time("14:00")
                .score(85)
                .scoreSargos(85)
                .isSafe(true)
                .verdict("Condicións óptimas (Moi bo momento)")
                .build();

        DayForecastResponse dayResponse = DayForecastResponse.builder()
                .portId(1L)
                .portName("A Coruña")
                .date("2026-09-12")
                .selectedSpecies(Species.SARGOS)
                .hourlyForecasts(List.of(hour))
                .build();

        when(forecastService.getForecastForPort(eq(1L), any(LocalDate.class), eq(Species.SARGOS)))
                .thenReturn(dayResponse);

        mockMvc.perform(get("/api/forecast/port/1")
                        .param("date", "2026-09-12")
                        .param("species", "SARGOS")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.portName").value("A Coruña"))
                .andExpect(jsonPath("$.data.hourlyForecasts[0].score").value(85));
    }
}