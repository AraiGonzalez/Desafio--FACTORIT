package com.ecommerceDesafio.cart_service.soap;

import com.ecommerceDesafio.cart_service.dto.*;
import com.ecommerceDesafio.cart_service.model.Customer;
import com.ecommerceDesafio.cart_service.service.CustomerService;
import lombok.RequiredArgsConstructor;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;
import org.w3c.dom.Element;

import java.util.List;
import java.util.stream.Collectors;
@Endpoint
@RequiredArgsConstructor
public class CustomerSoapEndpoint {

    private static final String NAMESPACE = "http://ecommerce.com/soap/customers";

    private final CustomerService customerService;

    // ── 1. Clientes VIP activos actualmente ──────────────────────────────────
    @PayloadRoot(namespace = NAMESPACE, localPart = "GetVipCustomersRequest")
    @ResponsePayload
    public GetVipCustomersResponse getVipCustomers(
            @RequestPayload GetVipCustomersRequest request) {

        GetVipCustomersResponse response = new GetVipCustomersResponse();
        response.setCustomers(toDTO(customerService.getVipCustomers()));
        return response;
    }

    // ── 2. Clientes que PASARON a ser VIP en un mes dado ─────────────────────
    // Request: GetNewVipByMonthRequest con year y month
    // Ejemplo: año 2024, mes 10 → devuelve clientes que ganaron VIP en octubre
    @PayloadRoot(namespace = NAMESPACE, localPart = "GetNewVipByMonthRequest")
    @ResponsePayload
    public GetVipByMonthResponse getNewVipByMonth(
            @RequestPayload Element request) {

        String yearText = request
                .getElementsByTagNameNS(
                        "http://ecommerce.com/soap/customers",
                        "year"
                )
                .item(0)
                .getTextContent();

        String monthText = request
                .getElementsByTagNameNS(
                        "http://ecommerce.com/soap/customers",
                        "month"
                )
                .item(0)
                .getTextContent();

        int year = Integer.parseInt(yearText);
        int month = Integer.parseInt(monthText);

        System.out.println("YEAR=" + year);
        System.out.println("MONTH=" + month);

        GetVipByMonthResponse response = new GetVipByMonthResponse();

        response.setCustomers(
                toDTO(customerService.getNewVipInMonth(year, month))
        );

        return response;
    }

    // ── 3. Clientes que DEJARON de ser VIP en un mes dado ────────────────────
    // Request: GetLostVipByMonthRequest con year y month
    // Ejemplo: año 2024, mes 11 → devuelve clientes que perdieron VIP en noviembre
    @PayloadRoot(namespace = NAMESPACE, localPart = "GetLostVipByMonthRequest")
    @ResponsePayload
    public GetVipByMonthResponse getLostVipByMonth(
            @RequestPayload Element request) {

        String yearText = request
                .getElementsByTagNameNS(
                        "http://ecommerce.com/soap/customers",
                        "year"
                )
                .item(0)
                .getTextContent();

        String monthText = request
                .getElementsByTagNameNS(
                        "http://ecommerce.com/soap/customers",
                        "month"
                )
                .item(0)
                .getTextContent();

        int year = Integer.parseInt(yearText);
        int month = Integer.parseInt(monthText);

        System.out.println("YEAR=" + year);
        System.out.println("MONTH=" + month);

        GetVipByMonthResponse response = new GetVipByMonthResponse();

        response.setCustomers(
                toDTO(customerService.getLostVipInMonth(year, month))
        );

        return response;
    }

    // ── Helper ────────────────────────────────────────────────────────────────
    private List<CustomerSoapDTO> toDTO(List<Customer> customers) {
        return customers.stream().map(c -> {
            CustomerSoapDTO dto = new CustomerSoapDTO();
            dto.setId(c.getId());
            dto.setName(c.getName());
            dto.setEmail(c.getEmail());
            dto.setVip(c.isVip());
            return dto;
        }).collect(Collectors.toList());
    }
}