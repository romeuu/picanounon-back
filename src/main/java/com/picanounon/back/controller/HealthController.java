package com.picanounon.back.controller;

import com.picanounon.back.dto.response.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api")
public class HealthController {

    @GetMapping("/health")
    public ResponseEntity<ApiResponse<Map<String, String>>> healthCheck() {
        Map<String, String> statusMap = Map.of(
                "status", "UP",
                "message", "Spring Boot API is running correctly"
        );
        return ResponseEntity.ok(ApiResponse.success("System is operational", statusMap));
    }
}
