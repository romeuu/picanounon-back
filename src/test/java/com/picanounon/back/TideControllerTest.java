package com.picanounon.back;

import com.picanounon.back.model.Port;
import com.picanounon.back.model.Tide;
import com.picanounon.back.model.TideType;
import com.picanounon.back.repository.PortRepository;
import com.picanounon.back.repository.TideRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.time.LocalTime;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class TideControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TideRepository tideRepository;

    @Autowired
    private PortRepository portRepository;

    @BeforeEach
    void setUp() {
        tideRepository.deleteAll();
    }

    @Test
    void shouldUploadCsvAndCalculateTidesWithPortOffset() throws Exception {
        // Find Baiona port dynamically
        Port baiona = portRepository.findAll().stream()
                .filter(p -> p.getAlias().equalsIgnoreCase("Baiona"))
                .findFirst()
                .orElseThrow();

        // Save a tide event for Vigo station at 05:25
        tideRepository.save(new Tide(null, baiona.getTideStation(), LocalDate.of(2026, 8, 29), LocalTime.of(5, 25), TideType.PLEAMAR, 3.45));

        // When requesting tides for Port Baiona (which has tideStation="Vigo" and tideOffsetMinutes=5)
        mockMvc.perform(get("/api/tides/port/" + baiona.getId() + "?date=2026-08-29"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data[0].portName").value("Baiona"))
                .andExpect(jsonPath("$.data[0].tideTime").value("05:30")); // 05:25 + 5 mins offset = 05:30
    }

    @Test
    void shouldReturnTidesForMurosPortAfterDirectorySync() throws Exception {
        // Trigger directory sync for real CSV files in data/mareas
        mockMvc.perform(post("/api/tides/sync-folder"))
                .andExpect(status().isOk());

        // Find Muros port dynamically
        Port muros = portRepository.findAll().stream()
                .filter(p -> p.getAlias().equalsIgnoreCase("Muros"))
                .findFirst()
                .orElseThrow();

        // Request tides for Muros port on 2026-08-29
        mockMvc.perform(get("/api/tides/port/" + muros.getId() + "?date=2026-08-29"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data[0].portName").value("Muros"))
                .andExpect(jsonPath("$.data[0].tideTime").value("05:35")); // Vilagarcía 05:30 + 5 mins offset for Muros = 05:35
    }

    @Test
    void shouldSyncFolderAndReturnSuccess() throws Exception {
        mockMvc.perform(post("/api/tides/sync-folder"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Directory synchronization completed"));
    }

    @Test
    void shouldBatchUploadCsvFiles() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
                "files",
                "Mareas_Vigo_2026.csv",
                "text/csv",
                "2026-08-29;06:00;Preamar;3.20".getBytes()
        );

        mockMvc.perform(multipart("/api/tides/upload").file(file))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }
}
