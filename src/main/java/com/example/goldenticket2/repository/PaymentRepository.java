package com.example.goldenticket2.repository;

import com.example.goldenticket2.entity.Payment;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
    Optional<Payment> findByReference(String reference);

    Optional<Payment> findByWompiTransactionId(String wompiTransactionId);
}
