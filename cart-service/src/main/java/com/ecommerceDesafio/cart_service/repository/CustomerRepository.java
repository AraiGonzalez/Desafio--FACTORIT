package com.ecommerceDesafio.cart_service.repository;

import com.ecommerceDesafio.cart_service.model.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CustomerRepository extends JpaRepository<Customer, Long> {

    List<Customer> findByVipTrue();

    @Query("SELECT DISTINCT p.customer FROM Purchase p " +
            "WHERE EXTRACT(YEAR FROM p.purchaseDate) = :year " +
            "AND EXTRACT(MONTH FROM p.purchaseDate) = :month " +
            "GROUP BY p.customer " +
            "HAVING SUM(p.total) > 10000")
    List<Customer> findCustomersWhoReachedVipInMonth(
            @Param("year") int year,
            @Param("month") int month);

}