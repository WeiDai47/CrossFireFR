package com.example.Crossfire.repository;
import java.util.List;

import com.example.Crossfire.PayoutStructure;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PayoutRepository extends JpaRepository<PayoutStructure, Long> {
    // Allows you to find payouts for a specific event
    List<PayoutStructure> findByRodeoEventId(Long eventId);
}