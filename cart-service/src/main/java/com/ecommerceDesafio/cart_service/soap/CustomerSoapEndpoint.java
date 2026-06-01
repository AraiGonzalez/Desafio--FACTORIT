package com.ecommerceDesafio.cart_service.soap;

import com.ecommerceDesafio.cart_service.dto.*;
import com.ecommerceDesafio.cart_service.model.Customer;
import com.ecommerceDesafio.cart_service.service.CustomerService;
import com.ecommerceDesafio.cart_service.soap.generated.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;

import java.util.List;
import java.util.stream.Collectors;
@Slf4j
@Endpoint
@RequiredArgsConstructor
public class CustomerSoapEndpoint {

    private static final String NAMESPACE = "http://ecommerce.com/soap/customers";

    private final CustomerService customerService;
    @PayloadRoot(namespace = NAMESPACE, localPart = "GetVipCustomersRequest")
    @ResponsePayload
    public GetVipCustomersResponse getVipCustomers(
            @RequestPayload GetVipCustomersRequest request) {

        log.info("SOAP - Consulta clientes VIP activos");

        GetVipCustomersResponse response = new GetVipCustomersResponse();
        response.getCustomer().addAll(toDTO(customerService.getVipCustomers()));

        log.info("SOAP - Se devuelven {} clientes VIP", response.getCustomer().size());
        return response;
    }

    @PayloadRoot(namespace = NAMESPACE, localPart = "GetNewVipByMonthRequest")
    @ResponsePayload
    public GetVipByMonthResponse getNewVipByMonth(
            @RequestPayload GetNewVipByMonthRequest request) {

        log.info("SOAP - Clientes que ganaron VIP en {}/{}", request.getMonth(), request.getYear());

        GetVipByMonthResponse response = new GetVipByMonthResponse();
        response.getCustomer().addAll(
                toDTO(customerService.getNewVipInMonth(request.getYear(), request.getMonth()))
        );

        log.info("SOAP - Se devuelven {} clientes que ganaron VIP", response.getCustomer().size());
        return response;
    }

    @PayloadRoot(namespace = NAMESPACE, localPart = "GetLostVipByMonthRequest")
    @ResponsePayload
    public GetVipByMonthResponse getLostVipByMonth(
            @RequestPayload GetLostVipByMonthRequest request) {

        log.info("SOAP - Clientes que perdieron VIP en {}/{}", request.getMonth(), request.getYear());

        GetVipByMonthResponse response = new GetVipByMonthResponse();
        response.getCustomer().addAll(
                toDTO(customerService.getLostVipInMonth(request.getYear(), request.getMonth()))
        );

        log.info("SOAP - Se devuelven {} clientes que perdieron VIP", response.getCustomer().size());
        return response;
    }

    private List<CustomerDTO> toDTO(List<Customer> customers) {
        return customers.stream().map(c -> {
            CustomerDTO dto = new CustomerDTO();
            dto.setId(c.getId());
            dto.setName(c.getName());
            dto.setEmail(c.getEmail());
            dto.setVip(c.isVip());
            return dto;
        }).collect(Collectors.toList());
    }
}