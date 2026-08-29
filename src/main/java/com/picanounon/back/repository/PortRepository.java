package com.picanounon.back.repository;

import com.picanounon.back.model.Port;
import com.picanounon.back.model.Zone;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PortRepository extends JpaRepository<Port, Long> {
    List<Port> findByZone(Zone zone);
}
