package com.picanounon.back.mapper;

import com.picanounon.back.dto.PortDTO;
import com.picanounon.back.dto.response.PortResponse;
import com.picanounon.back.model.Port;
import org.springframework.stereotype.Component;

@Component
public class PortMapper {

    public PortDTO toDTO(Port port) {
        if (port == null) {
            return null;
        }
        return new PortDTO(
                port.getId(),
                port.getIdZonaMG(),
                port.getAlias(),
                port.getName(),
                port.getZone(),
                port.getLat(),
                port.getLng(),
                port.getTideStation(),
                port.getTideOffsetMinutes()
        );
    }

    public Port toEntity(PortDTO dto) {
        if (dto == null) {
            return null;
        }
        return new Port(
                dto.getId(),
                dto.getIdZonaMG(),
                dto.getAlias(),
                dto.getName(),
                dto.getZone(),
                dto.getLat(),
                dto.getLng(),
                dto.getTideStation(),
                dto.getTideOffsetMinutes()
        );
    }

    public PortResponse toResponse(PortDTO dto) {
        if (dto == null) {
            return null;
        }
        return new PortResponse(
                dto.getId(),
                dto.getIdZonaMG(),
                dto.getAlias(),
                dto.getName(),
                dto.getZone(),
                dto.getLat(),
                dto.getLng(),
                dto.getTideStation(),
                dto.getTideOffsetMinutes()
        );
    }

    public PortResponse toResponse(Port port) {
        if (port == null) {
            return null;
        }
        return new PortResponse(
                port.getId(),
                port.getIdZonaMG(),
                port.getAlias(),
                port.getName(),
                port.getZone(),
                port.getLat(),
                port.getLng(),
                port.getTideStation(),
                port.getTideOffsetMinutes()
        );
    }
}
