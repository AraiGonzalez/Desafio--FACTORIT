package com.ecommerceDesafio.cart_service.repository;

import com.ecommerceDesafio.cart_service.model.SpecialDate;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;

public interface SpecialDateRepository extends JpaRepository<SpecialDate, Long> {
    boolean existsByDate(LocalDate date);
}
