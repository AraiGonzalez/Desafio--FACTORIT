package com.ecommerceDesafio.cart_service.service;

import com.ecommerceDesafio.cart_service.model.CartItem;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

@Component("COMMON")
public class CommonDiscountStrategy implements DiscountStrategy {

    /**
     * Carrito comun: sin descuento adicional por tipo.
     * Devuelve el subtotal sin modificar.
     */
    @Override
    public BigDecimal apply(BigDecimal subtotal, List<CartItem> items) {
        return subtotal;
    }
}