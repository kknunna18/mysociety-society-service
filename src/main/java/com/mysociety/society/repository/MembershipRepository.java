package com.mysociety.society.repository;
import com.mysociety.society.domain.HouseholdMembership; import java.util.*; import org.springframework.data.domain.*; import org.springframework.data.jpa.repository.JpaRepository;
public interface MembershipRepository extends JpaRepository<HouseholdMembership, UUID> { Optional<HouseholdMembership> findByIdAndSocietyId(UUID id, UUID societyId); Page<HouseholdMembership> findBySocietyId(UUID societyId, Pageable pageable); }
