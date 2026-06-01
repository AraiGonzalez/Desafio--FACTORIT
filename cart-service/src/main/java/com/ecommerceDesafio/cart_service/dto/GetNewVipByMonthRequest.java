package com.ecommerceDesafio.cart_service.dto;


import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "GetNewVipByMonthRequest",
        namespace = "http://ecommerce.com/soap/customers")
@XmlAccessorType(XmlAccessType.FIELD)
public class GetNewVipByMonthRequest {
    @XmlElement(name = "year", required = true)

    private int year;
    @XmlElement(name = "month", required = true)

    private int month;

    public int getYear()            { return year; }
    public void setYear(int year)   { this.year = year; }
    public int getMonth()           { return month; }
    public void setMonth(int month) { this.month = month; }
}
