package com.picanounon.back.model;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "ports")
public class Port {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Integer idZonaMG;
    private String alias;
    private String name;

    @Enumerated(EnumType.STRING)
    private Zone zone;

    private Double lat;
    private Double lng;

    public Port() {
    }

    public Port(Long id, Integer idZonaMG, String alias, String name, Zone zone, Double lat, Double lng) {
        this.id = id;
        this.idZonaMG = idZonaMG;
        this.alias = alias;
        this.name = name;
        this.zone = zone;
        this.lat = lat;
        this.lng = lng;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getIdZonaMG() {
        return idZonaMG;
    }

    public void setIdZonaMG(Integer idZonaMG) {
        this.idZonaMG = idZonaMG;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Zone getZone() {
        return zone;
    }

    public void setZone(Zone zone) {
        this.zone = zone;
    }

    public Double getLat() {
        return lat;
    }

    public void setLat(Double lat) {
        this.lat = lat;
    }

    public Double getLng() {
        return lng;
    }

    public void setLng(Double lng) {
        this.lng = lng;
    }
}
