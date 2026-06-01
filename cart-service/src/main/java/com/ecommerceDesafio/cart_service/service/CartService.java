package com.ecommerceDesafio.cart_service.service;

import com.ecommerceDesafio.cart_service.dto.CartResponseDTO;
import com.ecommerceDesafio.cart_service.model.*;
import com.ecommerceDesafio.cart_service.repository.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class CartService {
    private final CartTypeRepository cartTypeRepository;
    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final CustomerRepository customerRepository;
    private final ProductRepository productRepository;
    private final SpecialDateRepository specialDateRepository;
    private final PurchaseRepository purchaseRepository;
    private final PricingService pricingService;
    private final DateService dateService;
    private final CustomerService customerService;

    @Transactional
    public Cart createCart(Long customerId, LocalDate simulatedDate) {
        Customer customer = customerRepository.findById(customerId).orElseThrow(() -> new RuntimeException("Cliente no encontrado: " + customerId));

        LocalDate effectiveDate = dateService.getEffectiveDate(simulatedDate);

        boolean isVipOnThatDate;

        if (simulatedDate != null) {
            isVipOnThatDate = customerService.wasVipOnDate(customerId, effectiveDate);
        } else {
            isVipOnThatDate = customer.isVip();
        }

        String typeCode;
        if (isVipOnThatDate) {
            typeCode = "VIP";
        } else if (specialDateRepository.existsByDate(effectiveDate)) {
            typeCode = "SPECIAL_DATE";
        } else {
            typeCode = "COMMON";
        }

        CartType cartType = cartTypeRepository.findByCode(typeCode).orElseThrow(() -> new RuntimeException("Tipo no encontrado: " + typeCode));

        Cart cart = Cart.builder().customer(customer).type(cartType).status(CartStatus.OPEN).simulatedDate(effectiveDate).build();

        return cartRepository.save(cart);
    }


    @Transactional
    public void deleteCart(Long cartId) {
        Cart cart = getOpenCart(cartId);
        cartRepository.delete(cart);
    }


    @Transactional
    public Cart addProduct(Long cartId, Long productId, int quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("La cantidad debe ser mayor a 0");
        }

        Cart cart = getOpenCart(cartId);
        Product product = productRepository.findById(productId).orElseThrow(() -> new RuntimeException("Producto no encontrado: " + productId));

        cartItemRepository.findByCartIdAndProductId(cartId, productId).ifPresentOrElse(item -> item.setQuantity(item.getQuantity() + quantity), () -> {
            CartItem newItem = CartItem.builder().cart(cart).product(product).quantity(quantity).build();
            cart.getItems().add(newItem);
        });

        return cartRepository.save(cart);
    }

    @Transactional
    public Cart removeProduct(Long cartId, Long productId, int quantity) {
        Cart cart = getOpenCart(cartId);

        CartItem item = cartItemRepository.findByCartIdAndProductId(cartId, productId).orElseThrow(() -> new RuntimeException("Producto no encontrado en el carrito"));

        if (quantity >= item.getQuantity()) {
            cart.getItems().remove(item);
            cartItemRepository.delete(item);
        } else {
            item.setQuantity(item.getQuantity() - quantity);
        }

        return cartRepository.save(cart);
    }

    public CartResponseDTO getCartStatus(Long cartId) {
        Cart cart = cartRepository.findById(cartId).orElseThrow(() -> new RuntimeException("Carrito no encontrado: " + cartId));

        BigDecimal subtotal = pricingService.calculateSubtotal(cart);
        BigDecimal total = pricingService.calculateTotal(cart);
        BigDecimal discount = subtotal.subtract(total);

        return CartResponseDTO.builder().cartId(cart.getId()).customerId(cart.getCustomer().getId()).customerName(cart.getCustomer().getName()).cartType(cart.getType().getCode()).status(cart.getStatus().name()).items(cart.getItems()).subtotal(subtotal).totalDiscount(discount).total(total).build();
    }


    @Transactional
    public Purchase checkout(Long cartId) {
        Cart cart = getOpenCart(cartId);

        if (cart.getItems().isEmpty()) {
            throw new RuntimeException("No se puede finalizar un carrito vacío");
        }

        BigDecimal total = pricingService.calculateTotal(cart);
        LocalDate purchaseDate = dateService.getEffectiveDate(cart.getSimulatedDate());

        Purchase purchase = Purchase.builder().customer(cart.getCustomer()).cart(cart).total(total).purchaseDate(purchaseDate).build();
        purchaseRepository.save(purchase);

        cart.setStatus(CartStatus.COMPLETED);
        cartRepository.save(cart);

        customerService.recalculateVipStatus(cart.getCustomer().getId(), purchaseDate.getYear(), purchaseDate.getMonthValue());

        return purchase;
    }

    private Cart getOpenCart(Long cartId) {
        Cart cart = cartRepository.findById(cartId).orElseThrow(() -> new RuntimeException("Carrito no encontrado: " + cartId));
        if (cart.getStatus() != CartStatus.OPEN) {
            throw new RuntimeException("El carrito no está en estado OPEN");
        }
        return cart;
    }
}
