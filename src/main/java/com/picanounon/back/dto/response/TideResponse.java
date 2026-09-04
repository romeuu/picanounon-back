package com.picanounon.back.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.picanounon.back.model.TideType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TideResponse {
    private Long id;
    private String stationName;
    private String portName;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime tideDateTime;

    private TideType type;
    private Double height;
}
