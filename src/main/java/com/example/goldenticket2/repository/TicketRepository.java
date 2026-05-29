package com.example.goldenticket2.repository;

import com.example.goldenticket2.entity.Ticket;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TicketRepository extends JpaRepository<Ticket, Long> {
    Optional<Ticket> findByQrCode(String qrCode);

    List<Ticket> findByOwnerId(Long ownerId);
}
