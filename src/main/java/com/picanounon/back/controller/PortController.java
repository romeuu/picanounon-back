package com.picanounon.back.controller;

import com.picanounon.back.dto.PortDTO;
import com.picanounon.back.dto.response.ApiResponse;
import com.picanounon.back.dto.response.PortResponse;
import com.picanounon.back.mapper.PortMapper;
import com.picanounon.back.service.PortService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/ports")
public class PortController {

    private final PortService portService;
    private final PortMapper portMapper;

    public PortController(PortService portService, PortMapper portMapper) {
        this.portService = portService;
        this.portMapper = portMapper;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<PortResponse>>> getAllPorts() {
        List<PortDTO> ports = portService.getAllPorts();
        List<PortResponse> responses = ports.stream()
                .map(portMapper::toResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(ApiResponse.success(responses));
    }

    @GetMapping("/zones")
    public ResponseEntity<ApiResponse<List<String>>> getAllZones() {
        List<String> zones = portService.getAllZones();
        return ResponseEntity.ok(ApiResponse.success(zones));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<PortResponse>> getPortById(@PathVariable Long id) {
        return portService.getPortById(id)
                .map(portMapper::toResponse)
                .map(response -> ResponseEntity.ok(ApiResponse.success(response)))
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ApiResponse.error("Port not found with id: " + id)));
    }
}
