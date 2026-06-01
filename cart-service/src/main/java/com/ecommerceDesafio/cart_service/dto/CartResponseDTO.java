package com.ecommerceDesafio.cart_service.dto;

import com.ecommerceDesafio.cart_service.model.CartItem;
import lombok.*;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CartResponseDTO {
    private Long cartId;
    private Long customerId;
    private String customerName;
    private String cartType;
    private String status;
    private List<CartItem> items;
    private BigDecimal subtotal;
    private BigDecimal totalDiscount;
    private BigDecimal total;
}