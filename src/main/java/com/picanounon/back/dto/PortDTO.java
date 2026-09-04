package com.picanounon.back.dto;

import com.picanounon.back.model.Zone;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PortDTO {
    private Long id;
    private Integer idZonaMG;
    private String alias;
    private String name;
    private Zone zone;
    private Double lat;
    private Double lng;
    private String tideStation;

    @Builder.Default
    private Integer tideOffsetMinutes = 0;

    public PortDTO(Long id, Integer idZonaMG, String alias, String name, Zone zone, Double lat, Double lng) {
        this(id, idZonaMG, alias, name, zone, lat, lng, null, 0);
    }
}
