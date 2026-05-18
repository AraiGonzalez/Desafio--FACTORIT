package com.ecommerceDesafio.cart_service.repository;

import com.ecommerceDesafio.cart_service.model.CartType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CartTypeRepository extends JpaRepository<CartType, Long> {
    Optional<CartType> findByCode(String code);
}