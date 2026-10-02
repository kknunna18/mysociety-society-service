package com.mysociety.society.repository;

import com.mysociety.society.domain.SocietySetting;

import java.util.*;

import org.springframework.data.domain.*;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SettingRepository extends JpaRepository<SocietySetting, UUID> {
    Optional<SocietySetting> findByIdAndSocietyId(UUID id, UUID societyId);

    Page<SocietySetting> findBySocietyId(UUID societyId, Pageable pageable);
}
