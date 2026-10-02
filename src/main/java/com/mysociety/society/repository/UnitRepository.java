package com.mysociety.society.repository;

import com.mysociety.society.domain.Unit;

import java.util.*;

import org.springframework.data.domain.*;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UnitRepository extends JpaRepository<Unit, UUID> {
    Optional<Unit> findByIdAndSocietyId(UUID id, UUID societyId);

    Page<Unit> findBySocietyId(UUID societyId, Pageable pageable);
}
