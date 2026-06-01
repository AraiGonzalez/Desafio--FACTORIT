package com.ecommerceDesafio.cart_service.service;

import com.ecommerceDesafio.cart_service.model.CartItem;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;
@Component("SPECIAL_DATE")
public class SpecialDateDiscountStrategy implements DiscountStrategy {

    private static final BigDecimal SPECIAL_DATE_DISCOUNT = new BigDecimal("300");

    /**
     * Carrito fecha especial: descuento fijo de $300.
     */
    @Override
    public BigDecimal apply(BigDecimal subtotal, List<CartItem> items) {
        return subtotal.subtract(SPECIAL_DATE_DISCOUNT);
    }
}