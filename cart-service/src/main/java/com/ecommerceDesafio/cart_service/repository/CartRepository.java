package com.ecommerceDesafio.cart_service.repository;

import com.ecommerceDesafio.cart_service.model.Cart;
import com.ecommerceDesafio.cart_service.model.CartStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CartRepository extends JpaRepository<Cart, Long> {
    List<Cart> findByCustomerIdAndStatus(Long customerId, CartStatus status);
}
