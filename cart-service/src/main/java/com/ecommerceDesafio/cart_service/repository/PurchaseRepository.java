package com.ecommerceDesafio.cart_service.repository;

import com.ecommerceDesafio.cart_service.model.Purchase;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;

public interface PurchaseRepository extends JpaRepository<Purchase, Long> {
    // Suma lo que compró el cliente en un mes específico
// Sirve para saber si "merecía" ser VIP ese mes
    @Query("SELECT COALESCE(SUM(p.total), 0) FROM Purchase p " +
            "WHERE p.customer.id = :customerId " +
            "AND EXTRACT(YEAR FROM p.purchaseDate) = :year " +
            "AND EXTRACT(MONTH FROM p.purchaseDate) = :month")
    BigDecimal sumTotalByCustomerAndMonth(
            @Param("customerId") Long customerId,
            @Param("year") int year,
            @Param("month") int month);


}
