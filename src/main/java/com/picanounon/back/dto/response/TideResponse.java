package com.picanounon.back.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.picanounon.back.model.TideType;

import java.time.LocalDateTime;

public class TideResponse {
    private Long id;
    private String stationName;
    private String portName;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime tideDateTime;

    private TideType type;
    private Double height;

    public TideResponse() {
    }

    public TideResponse(Long id, String stationName, String portName, LocalDateTime tideDateTime, TideType type, Double height) {
        this.id = id;
        this.stationName = stationName;
        this.portName = portName;
        this.tideDateTime = tideDateTime;
        this.type = type;
        this.height = height;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getStationName() {
        return stationName;
    }

    public void setStationName(String stationName) {
        this.stationName = stationName;
    }

    public String getPortName() {
        return portName;
    }

    public void setPortName(String portName) {
        this.portName = portName;
    }

    public LocalDateTime getTideDateTime() {
        return tideDateTime;
    }

    public void setTideDateTime(LocalDateTime tideDateTime) {
        this.tideDateTime = tideDateTime;
    }

    public TideType getType() {
        return type;
    }

    public void setType(TideType type) {
        this.type = type;
    }

    public Double getHeight() {
        return height;
    }

    public void setHeight(Double height) {
        this.height = height;
    }
}
