package com.picanounon.back.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.picanounon.back.model.TideType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TideDTO {
    private Long id;
    private String stationName;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate tideDate;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm")
    private LocalTime tideTime;

    private TideType type;
    private Double height;
}
