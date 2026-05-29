package com.example.goldenticket2.service.impl;

import com.example.goldenticket2.dto.EventRequest;
import com.example.goldenticket2.dto.EventResponse;
import com.example.goldenticket2.dto.TicketTypeRequest;
import com.example.goldenticket2.entity.Event;
import com.example.goldenticket2.entity.EventStatus;
import com.example.goldenticket2.entity.TicketType;
import com.example.goldenticket2.entity.User;
import com.example.goldenticket2.exception.BadRequestException;
import com.example.goldenticket2.exception.ResourceNotFoundException;
import com.example.goldenticket2.mapper.EventMapper;
import com.example.goldenticket2.repository.EventRepository;
import com.example.goldenticket2.service.EventService;
import com.example.goldenticket2.util.SecurityUtils;
import java.time.LocalDateTime;
import java.util.ArrayList;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {

    private final EventRepository eventRepository;
    private final EventMapper eventMapper;

    @Override
    @Transactional
    public EventResponse create(EventRequest request) {
        User organizer = SecurityUtils.currentUser();
        Event event = eventMapper.toEntity(request);
        event.setOrganizer(organizer);
        applyTicketTypes(event, request);
        return eventMapper.toResponse(eventRepository.save(event));
    }

    @Override
    @Transactional
    public EventResponse update(Long id, EventRequest request) {
        Event event = findEvent(id);
        int soldTickets = event.getCapacity() - event.getAvailableCapacity();
        if (request.capacity() < soldTickets) {
            throw new BadRequestException("Capacity cannot be lower than tickets already reserved or sold");
        }
        eventMapper.update(event, request);
        event.setAvailableCapacity(request.capacity() - soldTickets);
        event.getTicketTypes().clear();
        applyTicketTypes(event, request);
        return eventMapper.toResponse(eventRepository.save(event));
    }

    @Override
    @Transactional
    public void delete(Long id) {
        Event event = findEvent(id);
        event.setStatus(EventStatus.CANCELLED);
        eventRepository.save(event);
    }

    @Override
    @Transactional(readOnly = true)
    public EventResponse getById(Long id) {
        return eventMapper.toResponse(findEvent(id));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<EventResponse> list(String search, boolean upcomingOnly, Pageable pageable) {
        String query = search == null ? "" : search;
        Page<Event> events = upcomingOnly
                ? eventRepository.findByStatusAndNameContainingIgnoreCaseAndEventDateAfter(EventStatus.PUBLISHED, query, LocalDateTime.now(), pageable)
                : eventRepository.findByNameContainingIgnoreCase(query, pageable);
        return events.map(eventMapper::toResponse);
    }

    private Event findEvent(Long id) {
        return eventRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Event not found with id " + id));
    }

    private void applyTicketTypes(Event event, EventRequest request) {
        if (request.ticketTypes() == null || request.ticketTypes().isEmpty()) {
            TicketType defaultType = TicketType.builder()
                    .category(com.example.goldenticket2.entity.TicketCategory.GENERAL)
                    .price(request.price())
                    .stock(request.capacity())
                    .availableStock(request.capacity())
                    .event(event)
                    .build();
            event.setTicketTypes(new ArrayList<>(java.util.List.of(defaultType)));
            return;
        }

        int totalStock = request.ticketTypes().stream().mapToInt(TicketTypeRequest::stock).sum();
        if (totalStock > request.capacity()) {
            throw new BadRequestException("Ticket type stock cannot exceed event capacity");
        }
        request.ticketTypes().forEach(type -> event.getTicketTypes().add(TicketType.builder()
                .category(type.category())
                .price(type.price())
                .stock(type.stock())
                .availableStock(type.stock())
                .event(event)
                .build()));
    }
}
