package com.ecommerceDesafio.cart_service.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "cart_types")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CartType {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String code;        // "COMMON", "VIP", "SPECIAL_DATE"

    @Column(nullable = false)
    private String description;
}