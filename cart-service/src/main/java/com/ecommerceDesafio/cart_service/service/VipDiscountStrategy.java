package com.ecommerceDesafio.cart_service.service;

import com.ecommerceDesafio.cart_service.model.CartItem;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

@Component("VIP")
public class VipDiscountStrategy implements DiscountStrategy {

    private static final BigDecimal VIP_FIXED_DISCOUNT = new BigDecimal("500");

    /**
     * Carrito VIP:
     * 1. Identifica el producto de menor precio
     * 2. Lo bonifica (descuenta su precio completo)
     * 3. Aplica descuento fijo de $500
     */
    @Override
    public BigDecimal apply(BigDecimal subtotal, List<CartItem> items) {
        BigDecimal cheapestPrice = items.stream()
                .map(item -> item.getProduct().getPrice())
                .min(BigDecimal::compareTo)
                .orElse(BigDecimal.ZERO);

        return subtotal
                .subtract(cheapestPrice)
                .subtract(VIP_FIXED_DISCOUNT);
    }
}

 
