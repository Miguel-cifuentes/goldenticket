package com.example.goldenticket2.repository;

import com.example.goldenticket2.entity.TicketType;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TicketTypeRepository extends JpaRepository<TicketType, Long> {
    List<TicketType> findByEventId(Long eventId);
}
