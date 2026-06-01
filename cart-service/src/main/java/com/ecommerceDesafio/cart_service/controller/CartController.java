package com.ecommerceDesafio.cart_service.controller;

import com.ecommerceDesafio.cart_service.dto.CartResponseDTO;
import com.ecommerceDesafio.cart_service.model.Cart;
import com.ecommerceDesafio.cart_service.model.Purchase;
import com.ecommerceDesafio.cart_service.service.CartService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
@Tag(name = "Cart", description = "Gestión del carrito de compras")
@CrossOrigin(origins = "*")
public class CartController {

    private final CartService cartService;

    @PostMapping
    @Operation(summary = "Crear un nuevo carrito",
            description = "Crea carrito y determina su tipo automáticamente (COMMON, VIP, SPECIAL_DATE)")
    public ResponseEntity<Cart> createCart(
            @RequestParam Long customerId,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate simulatedDate) {
        return ResponseEntity.ok(cartService.createCart(customerId, simulatedDate));
    }

    @DeleteMapping("/{cartId}")
    @Operation(summary = "Eliminar un carrito")
    public ResponseEntity<Void> deleteCart(@PathVariable Long cartId) {
        cartService.deleteCart(cartId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{cartId}/items")
    @Operation(summary = "Agregar producto al carrito")
    public ResponseEntity<Cart> addProduct(
            @PathVariable Long cartId,
            @RequestParam Long productId,
            @RequestParam(defaultValue = "1") int quantity) {
        return ResponseEntity.ok(cartService.addProduct(cartId, productId, quantity));
    }

    @DeleteMapping("/{cartId}/items")
    @Operation(summary = "Quitar producto del carrito")
    public ResponseEntity<Cart> removeProduct(
            @PathVariable Long cartId,
            @RequestParam Long productId,
            @RequestParam(defaultValue = "1") int quantity) {
        return ResponseEntity.ok(cartService.removeProduct(cartId, productId, quantity));
    }

    @GetMapping("/{cartId}/status")
    @Operation(summary = "Consultar estado del carrito",
            description = "Devuelve los items y el total calculado con todos los descuentos")
    public ResponseEntity<CartResponseDTO> getCartStatus(@PathVariable Long cartId) {
        return ResponseEntity.ok(cartService.getCartStatus(cartId));
    }

    @PostMapping("/{cartId}/checkout")
    @Operation(summary = "Finalizar la compra")
    public ResponseEntity<Purchase> checkout(@PathVariable Long cartId) {
        return ResponseEntity.ok(cartService.checkout(cartId));
    }
}