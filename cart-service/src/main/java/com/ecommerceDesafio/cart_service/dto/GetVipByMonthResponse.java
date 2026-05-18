package com.ecommerceDesafio.cart_service.dto;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

import java.util.ArrayList;
import java.util.List;

@XmlRootElement(name = "GetVipByMonthResponse",
        namespace = "http://ecommerce.com/soap/customers")
@XmlAccessorType(XmlAccessType.FIELD)
public class GetVipByMonthResponse {

    @XmlElement(name = "customer")
    private List<CustomerSoapDTO> customers = new ArrayList<>();

    public List<CustomerSoapDTO> getCustomers() {
        return customers;
    }

    public void setCustomers(List<CustomerSoapDTO> customers) {
        this.customers = customers;
    }
}