package com.example.goldenticket2.service;

import com.example.goldenticket2.dto.EventRequest;
import com.example.goldenticket2.dto.EventResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface EventService {
    EventResponse create(EventRequest request);

    EventResponse update(Long id, EventRequest request);

    void delete(Long id);

    EventResponse getById(Long id);

    Page<EventResponse> list(String search, boolean upcomingOnly, Pageable pageable);
}