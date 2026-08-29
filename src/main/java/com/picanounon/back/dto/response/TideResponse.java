package com.picanounon.back.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.picanounon.back.model.TideType;

import java.time.LocalDate;
import java.time.LocalTime;

public class TideResponse {
    private Long id;
    private String stationName;
    private String portName;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate tideDate;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm")
    private LocalTime tideTime;

    private TideType type;
    private Double height;

    public TideResponse() {
    }

    public TideResponse(Long id, String stationName, String portName, LocalDate tideDate, LocalTime tideTime, TideType type, Double height) {
        this.id = id;
        this.stationName = stationName;
        this.portName = portName;
        this.tideDate = tideDate;
        this.tideTime = tideTime;
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

    public LocalDate getTideDate() {
        return tideDate;
    }

    public void setTideDate(LocalDate tideDate) {
        this.tideDate = tideDate;
    }

    public LocalTime getTideTime() {
        return tideTime;
    }

    public void setTideTime(LocalTime tideTime) {
        this.tideTime = tideTime;
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
