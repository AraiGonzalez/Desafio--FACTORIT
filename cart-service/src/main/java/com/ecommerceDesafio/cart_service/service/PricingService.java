package com.ecommerceDesafio.cart_service.service;

import com.ecommerceDesafio.cart_service.model.Cart;
import com.ecommerceDesafio.cart_service.model.CartItem;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;


@Service
public class PricingService {

    public BigDecimal calculateTotal(Cart cart) {
        List<CartItem> items = cart.getItems();

        if (items.isEmpty()) {
            return BigDecimal.ZERO;
        }

        int totalQuantity = items.stream()
                .mapToInt(CartItem::getQuantity)
                .sum();

        BigDecimal subtotal = items.stream()
                .map(item -> item.getProduct().getPrice()
                        .multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // Regla 1: exactamente 4 productos → 25% de descuento
        if (totalQuantity == 4) {
            subtotal = subtotal.multiply(new BigDecimal("0.75"));
        }

        // Regla 2: más de 10 productos → descuento adicional de $100
        if (totalQuantity > 10) {
            subtotal = subtotal.subtract(new BigDecimal("100"));


            // Reglas por tipo — SIEMPRE aplican, independiente de la cantidad
            switch (cart.getType().getCode()) {
                case "SPECIAL_DATE":
                    subtotal = subtotal.subtract(new BigDecimal("300"));
                    break;
                case "VIP":
                    BigDecimal cheapest = items.stream()
                            .map(i -> i.getProduct().getPrice())
                            .min(BigDecimal::compareTo)
                            .orElse(BigDecimal.ZERO);
                    subtotal = subtotal.subtract(cheapest)
                            .subtract(new BigDecimal("500"));
                    break;
                default:
                    break;
            }
        }
        return subtotal.max(BigDecimal.ZERO);
    }

    public BigDecimal calculateSubtotal(Cart cart) {
        return cart.getItems().stream()
                .map(item -> item.getProduct().getPrice()
                        .multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}