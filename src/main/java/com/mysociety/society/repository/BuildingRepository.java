package com.mysociety.society.repository;
import com.mysociety.society.domain.Building; import java.util.*; import org.springframework.data.domain.*; import org.springframework.data.jpa.repository.JpaRepository;
public interface BuildingRepository extends JpaRepository<Building, UUID> { Optional<Building> findByIdAndSocietyId(UUID id, UUID societyId); Page<Building> findBySocietyId(UUID societyId, Pageable pageable); }
