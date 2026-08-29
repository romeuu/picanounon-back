package com.picanounon.back.repository;

import com.picanounon.back.model.Tide;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

public interface TideRepository extends JpaRepository<Tide, Long> {

    List<Tide> findByTideDateOrderByTideTimeAsc(LocalDate tideDate);

    Optional<Tide> findByStationNameAndTideDateAndTideTime(String stationName, LocalDate tideDate, LocalTime tideTime);
}
