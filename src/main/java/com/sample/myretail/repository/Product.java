package com.sample.myretail.repository;

import org.springframework.data.annotation.Id;

public class Product {
    @Id
    private long id;

    private Double value;
    private String currencyCode;

    public Product() {}

    public Product(long id, Double value, String currencyCode) {
        this.id = id;
        this.value = value;
        this.currencyCode = currencyCode;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Double getValue() {
        return value;
    }

    public void setValue(Double value) {
        this.value = value;
    }

    public String getCurrencyCode() {
        return currencyCode;
    }

    public void setCurrencyCode(String currencyCode) {
        this.currencyCode = currencyCode;
    }

    @Override
    public String toString() {
        return String.format(
                "Product[id=%s, value='%s', currencyCode='%s']",
                id, value, currencyCode);
    }
}
