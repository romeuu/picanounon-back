package com.picanounon.back.util;

import com.picanounon.back.model.Tide;
import com.picanounon.back.model.TideType;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Component
public class MeteogaliciaCsvParser {

    private static final List<DateTimeFormatter> DATE_FORMATTERS = List.of(
            DateTimeFormatter.ofPattern("yyyy-MM-dd"),
            DateTimeFormatter.ofPattern("dd/MM/yyyy"),
            DateTimeFormatter.ofPattern("yyyy/MM/dd"),
            DateTimeFormatter.ofPattern("dd-MM-yyyy")
    );

    private static final List<DateTimeFormatter> TIME_FORMATTERS = List.of(
            DateTimeFormatter.ofPattern("HH:mm:ss"),
            DateTimeFormatter.ofPattern("HH:mm"),
            DateTimeFormatter.ofPattern("H:mm")
    );

    public List<Tide> parse(InputStream inputStream, String fallbackStationName) throws Exception {
        List<Tide> tides = new ArrayList<>();
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
        String line;

        String currentStation = fallbackStationName;

        while ((line = reader.readLine()) != null) {
            line = line.trim();
            if (line.isEmpty() || line.startsWith("#") || line.startsWith("//")) {
                continue;
            }

            if (line.toLowerCase().startsWith("estacion") || line.toLowerCase().startsWith("estación")) {
                String[] parts = line.split("[:;=]");
                if (parts.length > 1) {
                    currentStation = parts[1].trim();
                }
                continue;
            }

            String delimiter = line.contains(";") ? ";" : (line.contains(",") ? "," : "\\t");
            String[] columns = line.split(delimiter);

            if (isHeader(columns)) {
                continue;
            }

            // Format 1: Meteogalicia annual line: Day;Month;Year;DayOfWeek;Time1;Height1;Time2;Height2;...
            if (isMeteogaliciaAnnualFormat(columns)) {
                parseMeteogaliciaAnnualRow(columns, currentStation, tides);
            }
            // Format 2: Column-based: Date;Time;Type;Height
            else if (columns.length >= 3) {
                parseStandardColumnRow(columns, currentStation, tides);
            }
        }
        return tides;
    }

    private boolean isMeteogaliciaAnnualFormat(String[] columns) {
        if (columns.length < 6) return false;
        try {
            int day = Integer.parseInt(columns[0].trim());
            int month = Integer.parseInt(columns[1].trim());
            int year = Integer.parseInt(columns[2].trim());
            return day >= 1 && day <= 31 && month >= 1 && month <= 12 && year >= 2000 && year <= 2100;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private void parseMeteogaliciaAnnualRow(String[] columns, String stationName, List<Tide> tides) {
        try {
            int day = Integer.parseInt(columns[0].trim());
            int month = Integer.parseInt(columns[1].trim());
            int year = Integer.parseInt(columns[2].trim());
            LocalDate date = LocalDate.of(year, month, day);

            // Time and height pairs start at index 4
            List<LocalTime> times = new ArrayList<>();
            List<Double> heights = new ArrayList<>();

            for (int i = 4; i + 1 < columns.length; i += 2) {
                LocalTime time = parseTime(columns[i].trim());
                Double height = parseDouble(columns[i + 1].trim());

                if (time != null && height != null) {
                    times.add(time);
                    heights.add(height);
                }
            }

            for (int i = 0; i < times.size(); i++) {
                LocalTime time = times.get(i);
                Double height = heights.get(i);

                TideType type;
                if (i == 0) {
                    if (heights.size() > 1) {
                        type = height > heights.get(1) ? TideType.PLEAMAR : TideType.BAJAMAR;
                    } else {
                        type = height >= 2.0 ? TideType.PLEAMAR : TideType.BAJAMAR;
                    }
                } else {
                    type = height > heights.get(i - 1) ? TideType.PLEAMAR : TideType.BAJAMAR;
                }

                Tide tide = new Tide(null, stationName, date, time, type, height);
                tides.add(tide);
            }
        } catch (Exception ignored) {
        }
    }

    private void parseStandardColumnRow(String[] columns, String stationName, List<Tide> tides) {
        try {
            LocalDate date = parseDate(columns[0].trim());
            LocalTime time = parseTime(columns[1].trim());
            TideType type = TideType.fromString(columns[2].trim());
            Double height = columns.length >= 4 ? parseDouble(columns[3].trim()) : 0.0;

            if (date != null && time != null && type != null) {
                Tide tide = new Tide(null, stationName, date, time, type, height);
                tides.add(tide);
            }
        } catch (Exception ignored) {
        }
    }

    private boolean isHeader(String[] columns) {
        String first = columns[0].trim().toLowerCase();
        return first.contains("fecha") || first.contains("data") || first.contains("date") || first.contains("dia");
    }

    private LocalDate parseDate(String text) {
        for (DateTimeFormatter formatter : DATE_FORMATTERS) {
            try {
                return LocalDate.parse(text, formatter);
            } catch (Exception ignored) {
            }
        }
        return null;
    }

    private LocalTime parseTime(String text) {
        for (DateTimeFormatter formatter : TIME_FORMATTERS) {
            try {
                return LocalTime.parse(text, formatter);
            } catch (Exception ignored) {
            }
        }
        return null;
    }

    private Double parseDouble(String text) {
        try {
            return Double.parseDouble(text.replace(",", "."));
        } catch (Exception e) {
            return 0.0;
        }
    }
}
