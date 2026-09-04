package com.picanounon.back.service;

import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

@Component
@Slf4j
public class TideDirectoryWatcher {

    private final TideService tideService;

    @Getter
    private final String watchDirPath;

    public TideDirectoryWatcher(TideService tideService,
                                @Value("${app.tides.watch-dir:data/mareas}") String watchDirSetting) {
        this.tideService = tideService;
        this.watchDirPath = watchDirSetting;
    }

    @PostConstruct
    public void initDirectory() {
        try {
            Path path = Paths.get(watchDirPath);
            if (!Files.exists(path)) {
                Files.createDirectories(path);
            }
            syncDirectory();
        } catch (IOException e) {
            log.error("Could not initialize tide watch directory: {}", e.getMessage());
        }
    }

    // Runs every hour to check for new/updated CSV files in data/mareas
    @Scheduled(cron = "0 0 * * * *")
    public Map<String, Integer> syncDirectory() {
        Path path = Paths.get(watchDirPath);
        if (Files.exists(path)) {
            return tideService.importFromDirectory(path);
        }
        return Map.of();
    }
}
