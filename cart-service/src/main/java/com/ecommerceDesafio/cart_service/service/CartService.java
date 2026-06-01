package com.ecommerceDesafio.cart_service.service;

import com.ecommerceDesafio.cart_service.dto.CartResponseDTO;
import com.ecommerceDesafio.cart_service.exception.BusinessException;
import com.ecommerceDesafio.cart_service.exception.ResourceNotFoundException;
import com.ecommerceDesafio.cart_service.model.*;
import com.ecommerceDesafio.cart_service.repository.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;

    @Slf4j
    @Service
    @RequiredArgsConstructor
    public class CartService {

        private final CartTypeRepository    cartTypeRepository;
        private final CartRepository        cartRepository;
        private final CartItemRepository    cartItemRepository;
        private final CustomerRepository    customerRepository;
        private final ProductRepository     productRepository;
        private final SpecialDateRepository specialDateRepository;
        private final PurchaseRepository    purchaseRepository;
        private final PricingService        pricingService;
        private final DateService           dateService;
        private final CustomerService       customerService;

        // ── Creacion ──────────────────────────────────────────────

        /**
         * Crea un nuevo carrito determinando automaticamente su tipo.
         *
         * Logica de prioridad (las promociones NO se combinan):
         *   1. Si el cliente es VIP en la fecha efectiva → tipo VIP
         *   2. Si la fecha efectiva es especial → tipo SPECIAL_DATE
         *   3. En cualquier otro caso → tipo COMMON
         *
         * Con fecha simulada: el VIP se calcula historicamente mirando
         * las compras del mes anterior a esa fecha.
         * Sin fecha simulada: se usa el campo is_vip actual del cliente.
         *
         * @param customerId    ID del cliente que crea el carrito
         * @param simulatedDate fecha opcional para simular (puede ser null)
         * @return carrito creado con su tipo asignado
         */
        @Transactional
        public Cart createCart(Long customerId, LocalDate simulatedDate) {
            log.info("Creando carrito para cliente {} con fecha simulada {}", customerId, simulatedDate);

            Customer customer = customerRepository.findById(customerId)
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Cliente no encontrado: " + customerId));

            LocalDate effectiveDate = dateService.getEffectiveDate(simulatedDate);

            // Determinar si el cliente era VIP en la fecha efectiva
            boolean isVipOnThatDate = (simulatedDate != null)
                    ? customerService.wasVipOnDate(customerId, effectiveDate)
                    : customer.isVip();

            // Determinar tipo de carrito con prioridad VIP > SPECIAL_DATE > COMMON
            String typeCode;
            if (isVipOnThatDate) {
                typeCode = "VIP";
            } else if (specialDateRepository.existsByDate(effectiveDate)) {
                typeCode = "SPECIAL_DATE";
            } else {
                typeCode = "COMMON";
            }

            CartType cartType = cartTypeRepository.findByCode(typeCode)
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Tipo de carrito no encontrado: " + typeCode));

            Cart cart = Cart.builder()
                    .customer(customer)
                    .type(cartType)
                    .status(CartStatus.OPEN)
                    .simulatedDate(effectiveDate)
                    .build();

            Cart saved = cartRepository.save(cart);
            log.info("Carrito {} creado con tipo {} para cliente {}",
                    saved.getId(), typeCode, customer.getName());
            return saved;
        }

        // ── Eliminacion ───────────────────────────────────────────

        /**
         * Elimina un carrito. Solo se pueden eliminar carritos OPEN.
         * Un carrito COMPLETED no se puede eliminar porque ya tiene
         * una compra registrada asociada.
         *
         * @param cartId ID del carrito a eliminar
         */
        @Transactional
        public void deleteCart(Long cartId) {
            log.info("Eliminando carrito {}", cartId);
            Cart cart = getOpenCart(cartId);
            cartRepository.delete(cart);
            log.info("Carrito {} eliminado", cartId);
        }

        // ── Items ─────────────────────────────────────────────────

        /**
         * Agrega un producto al carrito.
         * Si el producto ya existe en el carrito, incrementa la cantidad.
         * Si es nuevo, crea un CartItem nuevo.
         *
         * @param cartId    ID del carrito
         * @param productId ID del producto a agregar
         * @param quantity  cantidad a agregar (debe ser mayor a 0)
         * @return carrito actualizado con el nuevo item
         */
        @Transactional
        public Cart addProduct(Long cartId, Long productId, int quantity) {
            if (quantity <= 0) {
                throw new BusinessException("La cantidad debe ser mayor a 0");
            }

            Cart cart = getOpenCart(cartId);
            Product product = productRepository.findById(productId)
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Producto no encontrado: " + productId));

            cartItemRepository.findByCartIdAndProductId(cartId, productId)
                    .ifPresentOrElse(
                            // Si ya existe el item, suma la cantidad
                            item -> {
                                item.setQuantity(item.getQuantity() + quantity);
                                log.debug("Cantidad actualizada para producto {} en carrito {}",
                                        productId, cartId);
                            },
                            // Si no existe, crea un item nuevo
                            () -> {
                                CartItem newItem = CartItem.builder()
                                        .cart(cart)
                                        .product(product)
                                        .quantity(quantity)
                                        .build();
                                cart.getItems().add(newItem);
                                log.debug("Producto {} agregado al carrito {}", productId, cartId);
                            }
                    );

            return cartRepository.save(cart);
        }

        /**
         * Quita unidades de un producto del carrito.
         * Si la cantidad a quitar es igual o mayor a la existente,
         * elimina el item completo del carrito.
         *
         * @param cartId    ID del carrito
         * @param productId ID del producto a quitar
         * @param quantity  cantidad a quitar
         * @return carrito actualizado
         */
        @Transactional
        public Cart removeProduct(Long cartId, Long productId, int quantity) {
            Cart cart = getOpenCart(cartId);

            CartItem item = cartItemRepository.findByCartIdAndProductId(cartId, productId)
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Producto " + productId + " no encontrado en el carrito " + cartId));

            if (quantity >= item.getQuantity()) {
                cart.getItems().remove(item);
                cartItemRepository.delete(item);
                log.debug("Item del producto {} eliminado del carrito {}", productId, cartId);
            } else {
                item.setQuantity(item.getQuantity() - quantity);
                log.debug("Cantidad del producto {} reducida en carrito {}", productId, cartId);
            }

            return cartRepository.save(cart);
        }

        // ── Consulta ──────────────────────────────────────────────

        /**
         * Devuelve el estado actual del carrito con los totales calculados.
         * Incluye: items, subtotal sin descuentos, total de descuentos y total a pagar.
         *
         * @param cartId ID del carrito
         * @return DTO con toda la informacion del carrito
         */
        public CartResponseDTO getCartStatus(Long cartId) {
            Cart cart = cartRepository.findById(cartId)
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Carrito no encontrado: " + cartId));

            BigDecimal subtotal = pricingService.calculateSubtotal(cart);
            BigDecimal total    = pricingService.calculateTotal(cart);
            BigDecimal discount = subtotal.subtract(total);

            log.debug("Estado carrito {} - subtotal: {}, descuento: {}, total: {}",
                    cartId, subtotal, discount, total);

            return CartResponseDTO.builder()
                    .cartId(cart.getId())
                    .customerId(cart.getCustomer().getId())
                    .customerName(cart.getCustomer().getName())
                    .cartType(cart.getType().getCode())
                    .status(cart.getStatus().name())
                    .items(cart.getItems())
                    .subtotal(subtotal)
                    .totalDiscount(discount)
                    .total(total)
                    .build();
        }

        // ── Checkout ──────────────────────────────────────────────

        /**
         * Finaliza la compra del carrito en una transaccion atomica:
         *   1. Calcula el total con descuentos
         *   2. Registra la compra en la tabla purchases
         *   3. Marca el carrito como COMPLETED
         *   4. Recalcula el estado VIP del cliente para ese mes
         *
         * Si cualquier paso falla, toda la operacion se revierte
         * gracias a @Transactional. Esto garantiza consistencia.
         *
         * @param cartId ID del carrito a finalizar
         * @return registro de la compra creado
         */
        @Transactional
        public Purchase checkout(Long cartId) {
            log.info("Iniciando checkout del carrito {}", cartId);

            Cart cart = getOpenCart(cartId);

            if (cart.getItems().isEmpty()) {
                throw new BusinessException("No se puede finalizar un carrito vacio");
            }

            BigDecimal total       = pricingService.calculateTotal(cart);
            LocalDate purchaseDate = dateService.getEffectiveDate(cart.getSimulatedDate());

            // Registrar la compra con el precio real pagado (con descuentos)
            Purchase purchase = Purchase.builder()
                    .customer(cart.getCustomer())
                    .cart(cart)
                    .total(total)
                    .purchaseDate(purchaseDate)
                    .build();
            purchaseRepository.save(purchase);

            // Marcar carrito como completado
            cart.setStatus(CartStatus.COMPLETED);
            cartRepository.save(cart);

            // Recalcular estado VIP: si supero $10.000 este mes pasa a VIP,
            // si no compro nada pierde VIP
            customerService.recalculateVipStatus(
                    cart.getCustomer().getId(),
                    purchaseDate.getYear(),
                    purchaseDate.getMonthValue()
            );

            log.info("Checkout completado: carrito={}, cliente={}, total={}",
                    cartId, cart.getCustomer().getName(), total);

            return purchase;
        }

        // ── Helper privado ────────────────────────────────────────

        /**
         * Obtiene un carrito verificando que este en estado OPEN.
         * Centraliza la validacion de estado para no repetirla en cada metodo.
         *
         * @param cartId ID del carrito
         * @return carrito en estado OPEN
         * @throws ResourceNotFoundException si el carrito no existe
         * @throws BusinessException         si el carrito no esta OPEN
         */
        private Cart getOpenCart(Long cartId) {
            Cart cart = cartRepository.findById(cartId)
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Carrito no encontrado: " + cartId));

            if (cart.getStatus() != CartStatus.OPEN) {
                throw new BusinessException(
                        "El carrito " + cartId + " no esta en estado OPEN. Estado actual: "
                                + cart.getStatus());
            }
            return cart;
        }
    }