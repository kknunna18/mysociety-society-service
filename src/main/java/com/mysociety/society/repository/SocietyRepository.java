package com.mysociety.society.repository;
import com.mysociety.society.domain.Society; import java.util.*; import org.springframework.data.jpa.repository.JpaRepository;
public interface SocietyRepository extends JpaRepository<Society, UUID> { Optional<Society> findById(UUID id); }
