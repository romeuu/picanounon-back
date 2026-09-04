package com.picanounon.back.service;

import com.picanounon.back.dto.PortDTO;
import com.picanounon.back.mapper.PortMapper;
import com.picanounon.back.model.Zone;
import com.picanounon.back.repository.PortRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PortService {

    private final PortRepository portRepository;
    private final PortMapper portMapper;

    public List<PortDTO> getAllPorts() {
        return portRepository.findAll().stream()
                .map(portMapper::toDTO)
                .collect(Collectors.toList());
    }

    public Optional<PortDTO> getPortById(Long id) {
        return portRepository.findById(id).map(portMapper::toDTO);
    }

    public List<String> getAllZones() {
        return Arrays.stream(Zone.values())
                .map(Zone::getValue)
                .collect(Collectors.toList());
    }
}
