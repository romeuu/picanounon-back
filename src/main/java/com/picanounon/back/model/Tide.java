package com.picanounon.back.model;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Table(name = "tides", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"stationName", "tideDate", "tideTime"})
})
public class Tide {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String stationName;
    private LocalDate tideDate;
    private LocalTime tideTime;

    @Enumerated(EnumType.STRING)
    private TideType type;

    private Double height;

    public Tide() {
    }

    public Tide(Long id, String stationName, LocalDate tideDate, LocalTime tideTime, TideType type, Double height) {
        this.id = id;
        this.stationName = stationName;
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
