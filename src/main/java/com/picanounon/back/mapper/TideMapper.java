package com.picanounon.back.mapper;

import com.picanounon.back.dto.TideDTO;
import com.picanounon.back.dto.response.TideResponse;
import com.picanounon.back.model.Tide;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class TideMapper {

    public TideDTO toDTO(Tide tide) {
        if (tide == null) {
            return null;
        }
        return new TideDTO(
                tide.getId(),
                tide.getStationName(),
                tide.getTideDate(),
                tide.getTideTime(),
                tide.getType(),
                tide.getHeight()
        );
    }

    public Tide toEntity(TideDTO dto) {
        if (dto == null) {
            return null;
        }
        return new Tide(
                dto.getId(),
                dto.getStationName(),
                dto.getTideDate(),
                dto.getTideTime(),
                dto.getType(),
                dto.getHeight()
        );
    }

    public TideResponse toResponse(TideDTO dto, String portName, int offsetMinutes) {
        if (dto == null) {
            return null;
        }
        LocalDateTime tideDateTime = null;
        if (dto.getTideDate() != null && dto.getTideTime() != null) {
            tideDateTime = LocalDateTime.of(dto.getTideDate(), dto.getTideTime()).plusMinutes(offsetMinutes);
        }

        return new TideResponse(
                dto.getId(),
                dto.getStationName(),
                portName,
                tideDateTime,
                dto.getType(),
                dto.getHeight()
        );
    }
}
