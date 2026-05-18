package com.ecommerceDesafio.cart_service.service;

import com.ecommerceDesafio.cart_service.model.Customer;
import com.ecommerceDesafio.cart_service.repository.CustomerRepository;
import com.ecommerceDesafio.cart_service.repository.PurchaseRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CustomerService {

    private final CustomerRepository customerRepository;
    private final PurchaseRepository purchaseRepository;

    private static final BigDecimal VIP_THRESHOLD = new BigDecimal("10000");

    public List<Customer> getAllCustomers() {
        return customerRepository.findAll();
    }

    public List<Customer> getVipCustomers() {
        return customerRepository.findByVipTrue();
    }

    public List<Customer> getNewVipInMonth(int year, int month) {

        int prevMonth = month == 1 ? 12 : month - 1;
        int prevYear = month == 1 ? year - 1 : year;

        List<Customer> allCustomers = customerRepository.findAll();

        return allCustomers.stream().filter(customer -> {

            BigDecimal totalMesActual = purchaseRepository.sumTotalByCustomerAndMonth(customer.getId(), year, month);

            BigDecimal totalMesAnterior = purchaseRepository.sumTotalByCustomerAndMonth(customer.getId(), prevYear, prevMonth);

            boolean noEraVip = totalMesAnterior == null || totalMesAnterior.compareTo(VIP_THRESHOLD) <= 0;
            boolean ahoraEsVip = totalMesActual != null && totalMesActual.compareTo(VIP_THRESHOLD) > 0;

            return noEraVip && ahoraEsVip;
        }).collect(Collectors.toList());
    }

    public List<Customer> getLostVipInMonth(int year, int month) {

        System.out.println("YEAR=" + year + " MONTH=" + month);

        int prevMonth = month == 1 ? 12 : month - 1;
        int prevYear = month == 1 ? year - 1 : year;

        List<Customer> allCustomers = customerRepository.findAll();

        return allCustomers.stream().filter(customer -> {

            BigDecimal totalMesActual = purchaseRepository.sumTotalByCustomerAndMonth(customer.getId(), year, month);

            BigDecimal totalMesAnterior = purchaseRepository.sumTotalByCustomerAndMonth(customer.getId(), prevYear, prevMonth);

            boolean eraVip = totalMesAnterior != null && totalMesAnterior.compareTo(VIP_THRESHOLD) > 0;
            boolean dejoDeSerVip = totalMesActual == null || totalMesActual.compareTo(VIP_THRESHOLD) <= 0;


            return eraVip && dejoDeSerVip;
        }).collect(Collectors.toList());
    }

    @Transactional
    public void recalculateVipStatus(Long customerId, int year, int month) {
        Customer customer = customerRepository.findById(customerId).orElseThrow(() -> new RuntimeException("Cliente no encontrado"));

        BigDecimal monthTotal = purchaseRepository.sumTotalByCustomerAndMonth(customerId, year, month);

        if (monthTotal.compareTo(BigDecimal.ZERO) == 0) {
            customer.setVip(false);
        } else if (monthTotal.compareTo(VIP_THRESHOLD) > 0) {
            customer.setVip(true);
        }

        customerRepository.save(customer);
    }

    @Transactional
    public void recalculateAllVipStatuses(int year, int month) {
        List<Customer> customers = customerRepository.findAll();
        for (Customer customer : customers) {
            recalculateVipStatus(customer.getId(), year, month);
        }
    }

    public boolean wasVipOnDate(Long customerId, LocalDate date) {

        // Mes anterior
        LocalDate previousMonth = date.minusMonths(1);

        BigDecimal previousMonthTotal = purchaseRepository
                .sumTotalByCustomerAndMonth(
                        customerId,
                        previousMonth.getYear(),
                        previousMonth.getMonthValue()
                );

        BigDecimal currentMonthTotal = purchaseRepository
                .sumTotalByCustomerAndMonth(
                        customerId,
                        date.getYear(),
                        date.getMonthValue()
                );

        return previousMonthTotal.compareTo(VIP_THRESHOLD) > 0
                || currentMonthTotal.compareTo(VIP_THRESHOLD) > 0;
    }
}