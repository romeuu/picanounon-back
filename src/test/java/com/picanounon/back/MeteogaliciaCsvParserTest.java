package com.picanounon.back;

import com.picanounon.back.model.Tide;
import com.picanounon.back.model.TideType;
import com.picanounon.back.util.MeteogaliciaCsvParser;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class MeteogaliciaCsvParserTest {

    private final MeteogaliciaCsvParser parser = new MeteogaliciaCsvParser();

    @Test
    void shouldParseValidCsvStream() throws Exception {
        String csvContent = "Fecha;Hora;Tipo;Altura\n" +
                "2026-08-29;05:25:00;Preamar;3.45\n" +
                "2026-08-29;11:40:00;Baixamar;0.82\n" +
                "2026-08-29;17:50:00;Pleamar;3.60\n" +
                "2026-08-29;23:59:00;Bajamar;0.75\n";

        ByteArrayInputStream is = new ByteArrayInputStream(csvContent.getBytes(StandardCharsets.UTF_8));
        List<Tide> tides = parser.parse(is, "Vigo");

        assertEquals(4, tides.size());
        assertEquals("Vigo", tides.get(0).getStationName());
        assertEquals(LocalDate.of(2026, 8, 29), tides.get(0).getTideDate());
        assertEquals(LocalTime.of(5, 25), tides.get(0).getTideTime());
        assertEquals(TideType.PLEAMAR, tides.get(0).getType());
        assertEquals(3.45, tides.get(0).getHeight());

        assertEquals(TideType.BAJAMAR, tides.get(1).getType());
        assertEquals(0.82, tides.get(1).getHeight());
    }
}
