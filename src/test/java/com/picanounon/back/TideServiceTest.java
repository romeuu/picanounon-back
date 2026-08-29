package com.picanounon.back;

import com.picanounon.back.service.TideService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
class TideServiceTest {

    @Autowired
    private TideService tideService;

    @Test
    void shouldExtractStationNamesFromAvailableCsvFilenames() {
        assertEquals("A Coruña", tideService.extractStationFromFilename("A_Coruna_2026.csv"));
        assertEquals("A Guarda", tideService.extractStationFromFilename("Aguarda_2026.csv"));
        assertEquals("Ferrol Porto Exterior", tideService.extractStationFromFilename("Ferrol_Porto_Exterior_2026.csv"));
        assertEquals("Vigo", tideService.extractStationFromFilename("Vigo_2026.csv"));
        assertEquals("Vilagarcía", tideService.extractStationFromFilename("Vilagarcia_2026.csv"));
    }
}
