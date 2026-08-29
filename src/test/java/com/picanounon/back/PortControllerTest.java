package com.picanounon.back;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class PortControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void shouldReturnAllPortsWrappedInApiResponse() throws Exception {
        mockMvc.perform(get("/api/ports"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data.length()").value(53))
                .andExpect(jsonPath("$.data[0].name").value("A Guarda"))
                .andExpect(jsonPath("$.data[0].zone").value("Rías Baixas"));
    }

    @Test
    void shouldReturnPortByIdWrappedInApiResponse() throws Exception {
        mockMvc.perform(get("/api/ports/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").value(1))
                .andExpect(jsonPath("$.data.alias").value("A Guarda"))
                .andExpect(jsonPath("$.data.lat").value(41.9015));
    }

    @Test
    void shouldReturnAllZonesWrappedInApiResponse() throws Exception {
        mockMvc.perform(get("/api/ports/zones"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data[0]").value("Rías Baixas"));
    }

    @Test
    void shouldReturnNotFoundForNonExistentPort() throws Exception {
        mockMvc.perform(get("/api/ports/9999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Port not found with id: 9999"));
    }
}
