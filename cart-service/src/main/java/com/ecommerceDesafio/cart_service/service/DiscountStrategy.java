package com.ecommerceDesafio.cart_service.service;

import com.ecommerceDesafio.cart_service.model.CartItem;

import java.math.BigDecimal;
import java.util.List;

public interface  DiscountStrategy {
    BigDecimal apply(BigDecimal subtotal, List<CartItem> items);
}
