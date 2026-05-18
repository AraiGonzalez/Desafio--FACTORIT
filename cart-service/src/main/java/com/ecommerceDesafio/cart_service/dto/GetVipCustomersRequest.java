package com.ecommerceDesafio.cart_service.dto;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "GetVipCustomersRequest",
        namespace = "http://ecommerce.com/soap/customers")
@XmlAccessorType(XmlAccessType.FIELD)
public class GetVipCustomersRequest {
}