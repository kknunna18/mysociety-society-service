package com.mysociety.society.repository;

import com.mysociety.society.domain.Vehicle;

import java.util.*;

import org.springframework.data.domain.*;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VehicleRepository extends JpaRepository<Vehicle, UUID> {
    Optional<Vehicle> findByIdAndSocietyId(UUID id, UUID societyId);

    Page<Vehicle> findBySocietyId(UUID societyId, Pageable pageable);
}
