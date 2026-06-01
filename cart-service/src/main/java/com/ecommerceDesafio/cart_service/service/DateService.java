package com.ecommerceDesafio.cart_service.service;

import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class DateService {

    public LocalDate getEffectiveDate(LocalDate simulatedDate) {
        return simulatedDate != null ? simulatedDate : LocalDate.now();
    }
}
