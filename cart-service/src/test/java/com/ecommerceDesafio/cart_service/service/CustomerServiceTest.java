package com.ecommerceDesafio.cart_service.service;

import com.ecommerceDesafio.cart_service.model.Customer;
import com.ecommerceDesafio.cart_service.repository.CustomerRepository;
import com.ecommerceDesafio.cart_service.repository.PurchaseRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CustomerServiceTest {

    @Mock
    private CustomerRepository customerRepository;
    @Mock
    private PurchaseRepository purchaseRepository;

    @InjectMocks
    private CustomerService customerService;


    @Test
    @DisplayName("Cliente que compro mas de 10000 en el mes pasa a ser VIP")
    void customerWithMoreThan10000BecomesVip() {
        Customer customer = Customer.builder().id(1L).vip(false).build();
        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));
        when(purchaseRepository.sumTotalByCustomerAndMonth(1L, 2024, 11))
                .thenReturn(new BigDecimal("15000"));

        customerService.recalculateVipStatus(1L, 2024, 11);

        verify(customerRepository).save(argThat(Customer::isVip));
    }

    @Test
    @DisplayName("Cliente VIP que no compro en el mes pierde el estado VIP")
    void vipCustomerWithNoPurchasesLosesVip() {
        Customer customer = Customer.builder().id(1L).vip(true).build();
        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));
        when(purchaseRepository.sumTotalByCustomerAndMonth(1L, 2024, 11))
                .thenReturn(BigDecimal.ZERO);

        customerService.recalculateVipStatus(1L, 2024, 11);

        verify(customerRepository).save(argThat(c -> !c.isVip()));
    }

    @Test
    @DisplayName("Cliente que compro entre 0 y 10000 no cambia de estado")
    void customerBetweenZeroAnd10000DoesNotChangeStatus() {
        Customer customer = Customer.builder().id(1L).vip(false).build();
        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));
        when(purchaseRepository.sumTotalByCustomerAndMonth(1L, 2024, 11))
                .thenReturn(new BigDecimal("5000"));

        customerService.recalculateVipStatus(1L, 2024, 11);

        verify(customerRepository).save(argThat(c -> !c.isVip()));
    }

    @Test
    @DisplayName("Cliente que compro exactamente 10000 no alcanza el umbral VIP")
    void customerWithExactly10000DoesNotBecomeVip() {
        // La regla es MAS de $10.000 — exactamente $10.000 no alcanza
        Customer customer = Customer.builder().id(1L).vip(false).build();
        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));
        when(purchaseRepository.sumTotalByCustomerAndMonth(1L, 2024, 11))
                .thenReturn(new BigDecimal("10000"));

        customerService.recalculateVipStatus(1L, 2024, 11);

        verify(customerRepository).save(argThat(c -> !c.isVip()));
    }

    @Test
    @DisplayName("Cliente inexistente lanza excepcion al recalcular VIP")
    void nonExistentCustomerThrowsException() {
        when(customerRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class,
                () -> customerService.recalculateVipStatus(99L, 2024, 11));
    }


    @Test
    @DisplayName("wasVipOnDate devuelve true si el mes anterior supero 10000")
    void wasVipOnDateReturnsTrueIfPreviousMonthExceeded10000() {
        LocalDate date = LocalDate.of(2024, 11, 5);

        when(purchaseRepository.sumTotalByCustomerAndMonth(1L, 2024, 10))
                .thenReturn(new BigDecimal("11000"));

        boolean result = customerService.wasVipOnDate(1L, date);

        assertTrue(result);
        verify(purchaseRepository).sumTotalByCustomerAndMonth(1L, 2024, 10);
    }

    @Test
    @DisplayName("wasVipOnDate devuelve false si el mes anterior no supero 10000")
    void wasVipOnDateReturnsFalseIfNeitherMonthReachesThreshold() {

        LocalDate date = LocalDate.of(2024, 6, 15);

        // Mayo
        when(purchaseRepository.sumTotalByCustomerAndMonth(1L, 2024, 5))
                .thenReturn(new BigDecimal("3000"));

        when(purchaseRepository.sumTotalByCustomerAndMonth(1L, 2024, 6))
                .thenReturn(new BigDecimal("4000"));

        boolean result = customerService.wasVipOnDate(1L, date);

        assertFalse(result);
    }

    @Test
    @DisplayName("wasVipOnDate devuelve false si ni el mes anterior ni el actual tienen compras suficientes")
    void wasVipOnDateReturnsFalseIfNoPurchasesPreviousMonth() {

        LocalDate date = LocalDate.of(2024, 12, 1);

        // Noviembre 2024
        when(purchaseRepository.sumTotalByCustomerAndMonth(1L, 2024, 11))
                .thenReturn(BigDecimal.ZERO);

        // Diciembre 2024
        when(purchaseRepository.sumTotalByCustomerAndMonth(1L, 2024, 12))
                .thenReturn(BigDecimal.ZERO);

        boolean result = customerService.wasVipOnDate(1L, date);

        assertFalse(result);
    }

    @Test
    @DisplayName("wasVipOnDate calcula correctamente el cambio de anio")
    void wasVipOnDateHandlesYearChangeCorrectly() {
        LocalDate date = LocalDate.of(2025, 1, 10);

        when(purchaseRepository.sumTotalByCustomerAndMonth(1L, 2024, 12))
                .thenReturn(new BigDecimal("12000"));

        boolean result = customerService.wasVipOnDate(1L, date);

        assertTrue(result);
        verify(purchaseRepository).sumTotalByCustomerAndMonth(1L, 2024, 12);
    }

    @Test
    @DisplayName("wasVipOnDate devuelve false si ninguno de los meses supera exactamente 10000")
    void wasVipOnDateWithExactly10000ReturnsFalse() {

        LocalDate date = LocalDate.of(2024, 11, 5);

        when(purchaseRepository.sumTotalByCustomerAndMonth(1L, 2024, 10))
                .thenReturn(new BigDecimal("10000"));

        when(purchaseRepository.sumTotalByCustomerAndMonth(1L, 2024, 11))
                .thenReturn(new BigDecimal("10000"));

        boolean result = customerService.wasVipOnDate(1L, date);

        assertFalse(result);
    }
}