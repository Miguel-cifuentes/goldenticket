package com.example.goldenticket2.repository;

import com.example.goldenticket2.entity.Event;
import com.example.goldenticket2.entity.EventStatus;
import java.time.LocalDateTime;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EventRepository extends JpaRepository<Event, Long> {
    Page<Event> findByStatusAndNameContainingIgnoreCaseAndEventDateAfter(
            EventStatus status,
            String name,
            LocalDateTime date,
            Pageable pageable
    );

    Page<Event> findByNameContainingIgnoreCase(String name, Pageable pageable);
}