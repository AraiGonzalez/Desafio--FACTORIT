package com.ecommerceDesafio.cart_service.service;

import com.ecommerceDesafio.cart_service.model.Cart;
import com.ecommerceDesafio.cart_service.model.CartStatus;
import com.ecommerceDesafio.cart_service.model.CartType;
import com.ecommerceDesafio.cart_service.model.Customer;
import com.ecommerceDesafio.cart_service.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CartServiceTest {

    @Mock
    private CartRepository cartRepository;
    @Mock
    private CartItemRepository cartItemRepository;
    @Mock
    private CustomerRepository customerRepository;
    @Mock
    private ProductRepository productRepository;
    @Mock
    private SpecialDateRepository specialDateRepository;
    @Mock
    private PurchaseRepository purchaseRepository;
    @Mock
    private CartTypeRepository cartTypeRepository;
    @Mock
    private PricingService pricingService;
    @Mock
    private DateService dateService;
    @Mock
    private CustomerService customerService;

    @InjectMocks
    private CartService cartService;

    private Customer regularCustomer;
    private Customer vipCustomer;

    private CartType cartTypeOf(String code) {
        CartType t = new CartType();
        t.setCode(code);
        return t;
    }

    @BeforeEach
    void setUp() {
        regularCustomer = Customer.builder()
                .id(1L).name("Juan").email("juan@mail.com").vip(false).build();
        vipCustomer = Customer.builder()
                .id(2L).name("Maria").email("maria@mail.com").vip(true).build();
    }


    @Test
    @DisplayName("Sin fecha simulada cliente normal crea carrito COMMON")
    void noSimulatedDateRegularCustomerCreatesCommonCart() {
        LocalDate today = LocalDate.of(2024, 6, 15);

        when(customerRepository.findById(1L)).thenReturn(Optional.of(regularCustomer));
        when(dateService.getEffectiveDate(null)).thenReturn(today);
        when(specialDateRepository.existsByDate(today)).thenReturn(false);
        when(cartTypeRepository.findByCode("COMMON")).thenReturn(Optional.of(cartTypeOf("COMMON")));
        when(cartRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        Cart result = cartService.createCart(1L, null);

        assertEquals("COMMON", result.getType().getCode());
        // Sin fecha simulada no se llama a wasVipOnDate
        verify(customerService, never()).wasVipOnDate(any(), any());
    }

    @Test
    @DisplayName("Sin fecha simulada cliente VIP crea carrito VIP")
    void noSimulatedDateVipCustomerCreatesVipCart() {
        LocalDate today = LocalDate.of(2024, 6, 15);

        when(customerRepository.findById(2L)).thenReturn(Optional.of(vipCustomer));
        when(dateService.getEffectiveDate(null)).thenReturn(today);
        when(cartTypeRepository.findByCode("VIP")).thenReturn(Optional.of(cartTypeOf("VIP")));
        when(cartRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        Cart result = cartService.createCart(2L, null);

        assertEquals("VIP", result.getType().getCode());
    }

    @Test
    @DisplayName("Con fecha simulada usa historial VIP no el campo is_vip actual")
    void withSimulatedDateUsesHistoricalVipNotCurrentField() {
        // Maria tiene is_vip=true actualmente PERO en la fecha simulada no era VIP
        LocalDate simulatedDate = LocalDate.of(2024, 6, 15);

        when(customerRepository.findById(2L)).thenReturn(Optional.of(vipCustomer));
        when(dateService.getEffectiveDate(simulatedDate)).thenReturn(simulatedDate);
        // En esa fecha histórica NO era VIP
        when(customerService.wasVipOnDate(2L, simulatedDate)).thenReturn(false);
        when(specialDateRepository.existsByDate(simulatedDate)).thenReturn(false);
        when(cartTypeRepository.findByCode("COMMON")).thenReturn(Optional.of(cartTypeOf("COMMON")));
        when(cartRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        Cart result = cartService.createCart(2L, simulatedDate);

        assertEquals("COMMON", result.getType().getCode());
        verify(customerService).wasVipOnDate(2L, simulatedDate);
    }

    @Test
    @DisplayName("Con fecha simulada cliente era VIP historicamente crea carrito VIP")
    void withSimulatedDateClientWasHistoricallyVipCreatesVipCart() {
        LocalDate simulatedDate = LocalDate.of(2024, 11, 5);

        when(customerRepository.findById(1L)).thenReturn(Optional.of(regularCustomer));
        when(dateService.getEffectiveDate(simulatedDate)).thenReturn(simulatedDate);
        // En esa fecha SÍ era VIP (compró +$10000 el mes anterior)
        when(customerService.wasVipOnDate(1L, simulatedDate)).thenReturn(true);
        when(cartTypeRepository.findByCode("VIP")).thenReturn(Optional.of(cartTypeOf("VIP")));
        when(cartRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        Cart result = cartService.createCart(1L, simulatedDate);

        assertEquals("VIP", result.getType().getCode());
    }

    @Test
    @DisplayName("Con fecha simulada en fecha especial y cliente no VIP crea SPECIAL_DATE")
    void withSimulatedDateSpecialDateNonVipCreatesSpecialDateCart() {
        LocalDate navidad = LocalDate.of(2024, 12, 25);

        when(customerRepository.findById(1L)).thenReturn(Optional.of(regularCustomer));
        when(dateService.getEffectiveDate(navidad)).thenReturn(navidad);
        when(customerService.wasVipOnDate(1L, navidad)).thenReturn(false);
        when(specialDateRepository.existsByDate(navidad)).thenReturn(true);
        when(cartTypeRepository.findByCode("SPECIAL_DATE"))
                .thenReturn(Optional.of(cartTypeOf("SPECIAL_DATE")));
        when(cartRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        Cart result = cartService.createCart(1L, navidad);

        assertEquals("SPECIAL_DATE", result.getType().getCode());
    }

    @Test
    @DisplayName("VIP historico tiene prioridad sobre fecha especial")
    void historicalVipHasPriorityOverSpecialDate() {
        LocalDate navidad = LocalDate.of(2024, 12, 25);

        when(customerRepository.findById(1L)).thenReturn(Optional.of(regularCustomer));
        when(dateService.getEffectiveDate(navidad)).thenReturn(navidad);
        // Era VIP en esa fecha
        when(customerService.wasVipOnDate(1L, navidad)).thenReturn(true);
        when(cartTypeRepository.findByCode("VIP")).thenReturn(Optional.of(cartTypeOf("VIP")));
        when(cartRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        Cart result = cartService.createCart(1L, navidad);

        assertEquals("VIP", result.getType().getCode());
        verify(specialDateRepository, never()).existsByDate(any());
    }


    @Test
    @DisplayName("Cliente inexistente lanza excepcion al crear carrito")
    void nonExistentCustomerThrowsException() {
        when(customerRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> cartService.createCart(99L, null));
    }

    @Test
    @DisplayName("Eliminar carrito COMPLETED lanza excepcion")
    void deleteCompletedCartThrowsException() {
        Cart completed = Cart.builder().id(1L).status(CartStatus.COMPLETED).build();
        when(cartRepository.findById(1L)).thenReturn(Optional.of(completed));
        assertThrows(RuntimeException.class, () -> cartService.deleteCart(1L));
    }

    @Test
    @DisplayName("Agregar producto con cantidad cero lanza excepcion")
    void addProductZeroQuantityThrowsException() {
        assertThrows(IllegalArgumentException.class,
                () -> cartService.addProduct(1L, 1L, 0));
    }

    @Test
    @DisplayName("Agregar producto con cantidad negativa lanza excepcion")
    void addProductNegativeQuantityThrowsException() {
        assertThrows(IllegalArgumentException.class,
                () -> cartService.addProduct(1L, 1L, -3));
    }

    @Test
    @DisplayName("Carrito inexistente lanza excepcion al agregar producto")
    void addProductToNonExistentCartThrowsException() {
        when(cartRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class,
                () -> cartService.addProduct(99L, 1L, 1));
    }
}
