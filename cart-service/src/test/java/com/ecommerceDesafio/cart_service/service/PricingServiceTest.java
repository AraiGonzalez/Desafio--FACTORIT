package com.ecommerceDesafio.cart_service.service;

import com.ecommerceDesafio.cart_service.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PricingServiceTest {

    private PricingService pricingService;

    @BeforeEach
    void setUp() {
        pricingService = new PricingService();
    }

    private CartType buildCartType(String code) {
        CartType type = new CartType();
        type.setCode(code);
        return type;
    }

    private CartItem buildItem(double price, int quantity) {
        Product product = new Product();
        product.setPrice(BigDecimal.valueOf(price));
        CartItem item = new CartItem();
        item.setProduct(product);
        item.setQuantity(quantity);
        return item;
    }

    private Cart buildCart(String typeCode, List<CartItem> items) {
        Cart cart = new Cart();
        cart.setType(buildCartType(typeCode));
        cart.setStatus(CartStatus.OPEN);
        cart.setItems(new ArrayList<>(items));
        return cart;
    }


    @Test
    @DisplayName("Carrito SPECIAL_DATE sin mas de 10 productos no aplica ningun descuento")
    void specialDateCartWithoutTenProductsNoDiscount() {
        // La regla SPECIAL_DATE solo aplica si hay MAS de 10 productos
        // 5 x $200 = $1000 — sin descuento
        Cart cart = buildCart("SPECIAL_DATE", List.of(buildItem(200.0, 5)));
        assertEquals(0, new BigDecimal("1000.00").compareTo(
                pricingService.calculateTotal(cart)));
    }

    @Test
    @DisplayName("Carrito SPECIAL_DATE con mas de 10 productos descuenta 100 y 300")
    void specialDateMoreThanTenAppliesBothDiscounts() {
        // 11 x $200 = $2200 - $100 (mas de 10) - $300 (fecha especial) = $1800
        Cart cart = buildCart("SPECIAL_DATE", List.of(buildItem(200.0, 11)));
        assertEquals(0, new BigDecimal("1800.00").compareTo(
                pricingService.calculateTotal(cart)));
    }


    @Test
    @DisplayName("Carrito VIP sin mas de 10 productos no aplica descuento VIP")
    void vipCartWithoutTenProductsNoVipDiscount() {
        // La regla VIP solo aplica si hay MAS de 10 productos
        // $1000 + $100 = $1100 — sin descuento
        List<CartItem> items = List.of(buildItem(1000.0, 1), buildItem(100.0, 1));
        Cart cart = buildCart("VIP", items);
        assertEquals(0, new BigDecimal("1100.00").compareTo(
                pricingService.calculateTotal(cart)));
    }

    @Test
    @DisplayName("Carrito VIP con mas de 10 productos descuenta 100 el mas barato y 500")
    void vipCartMoreThanTenAppliesAllDiscounts() {
        // 6 x $100 + 6 x $50 = $600 + $300 = $900 (12 productos en total)
        // - $100 (mas de 10) - $50 (mas barato) - $500 (VIP) = $250
        List<CartItem> items = List.of(buildItem(100.0, 6), buildItem(50.0, 6));
        Cart cart = buildCart("VIP", items);
        assertEquals(0, new BigDecimal("250.00").compareTo(
                pricingService.calculateTotal(cart)));
    }

    @Test
    @DisplayName("Carrito VIP identifica correctamente el producto mas barato entre varios")
    void vipCartCorrectlyIdentifiesCheapest() {
        // 4 x $500 + 4 x $200 + 4 x $50 = $2000 + $800 + $200 = $3000 (12 productos)
        // - $100 (mas de 10) - $50 (mas barato) - $500 (VIP) = $2350
        List<CartItem> items = List.of(
                buildItem(500.0, 4),
                buildItem(200.0, 4),
                buildItem(50.0, 4)
        );
        Cart cart = buildCart("VIP", items);
        assertEquals(0, new BigDecimal("2350.00").compareTo(
                pricingService.calculateTotal(cart)));
    }

    @Test
    @DisplayName("Carrito VIP con mas de 10 productos no queda negativo")
    void vipCartNeverGoesNegative() {
        // 11 x $10 = $110
        // - $100 (mas de 10) - $10 (mas barato) - $500 = negativo → $0
        Cart cart = buildCart("VIP", List.of(buildItem(10.0, 11)));
        assertEquals(0, BigDecimal.ZERO.compareTo(
                pricingService.calculateTotal(cart)));
    }
}