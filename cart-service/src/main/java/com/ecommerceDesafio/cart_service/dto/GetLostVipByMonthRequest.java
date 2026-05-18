package com.ecommerceDesafio.cart_service.dto;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@XmlRootElement(
        name = "GetLostVipByMonthRequest",
        namespace = "http://ecommerce.com/soap/customers"
)
@XmlAccessorType(XmlAccessType.FIELD)
public class GetLostVipByMonthRequest {

    @XmlElement(required = true)
    private int year;

    @XmlElement(required = true)
    private int month;

    public GetLostVipByMonthRequest() {
    }

}
