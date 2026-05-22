package com.example.goldenticket2.repository;

import com.example.goldenticket2.entity.Purchase;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PurchaseRepository extends JpaRepository<Purchase, Long> {
    Optional<Purchase> findByReference(String reference);
}