package com.picanounon.back.model;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "ports")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
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

    private String tideStation;

    @Builder.Default
    private Integer tideOffsetMinutes = 0;

    public Port(Long id, Integer idZonaMG, String alias, String name, Zone zone, Double lat, Double lng) {
        this(id, idZonaMG, alias, name, zone, lat, lng, null, 0);
    }
}
