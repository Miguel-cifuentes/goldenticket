package com.example.goldenticket2.mapper;

import com.example.goldenticket2.dto.EventRequest;
import com.example.goldenticket2.dto.EventResponse;
import com.example.goldenticket2.dto.TicketTypeResponse;
import com.example.goldenticket2.entity.Event;
import com.example.goldenticket2.entity.EventStatus;
import com.example.goldenticket2.entity.TicketType;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class EventMapper {

    public Event toEntity(EventRequest request) {
        return Event.builder()
                .name(request.name())
                .description(request.description())
                .eventDate(request.date())
                .location(request.location())
                .price(request.price())
                .capacity(request.capacity())
                .availableCapacity(request.capacity())
                .imageUrl(request.image())
                .status(EventStatus.PUBLISHED)
                .build();
    }

    public void update(Event event, EventRequest request) {
        event.setName(request.name());
        event.setDescription(request.description());
        event.setEventDate(request.date());
        event.setLocation(request.location());
        event.setPrice(request.price());
        event.setCapacity(request.capacity());
        event.setImageUrl(request.image());
    }

    public EventResponse toResponse(Event event) {
        List<TicketTypeResponse> types = event.getTicketTypes().stream()
                .map(this::toTicketTypeResponse)
                .toList();
        return new EventResponse(
                event.getId(),
                event.getName(),
                event.getDescription(),
                event.getEventDate(),
                event.getLocation(),
                event.getPrice(),
                event.getCapacity(),
                event.getAvailableCapacity(),
                event.getImageUrl(),
                event.getStatus(),
                event.getOrganizer().getId(),
                event.getOrganizer().getFullName(),
                types
        );
    }

    public TicketTypeResponse toTicketTypeResponse(TicketType ticketType) {
        return new TicketTypeResponse(
                ticketType.getId(),
                ticketType.getCategory(),
                ticketType.getPrice(),
                ticketType.getStock(),
                ticketType.getAvailableStock()
        );
    }
}
