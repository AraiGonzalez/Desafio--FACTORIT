package com.ecommerceDesafio.cart_service.repository;

import com.ecommerceDesafio.cart_service.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Long> {
}
