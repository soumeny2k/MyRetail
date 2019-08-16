package com.sample.myretail.repository;

import org.springframework.data.annotation.Id;

/**
 * This is the pojo which represents the DB entity in MongoDB
 */
public class Product {
    @Id
    private long id;
    private double value;
    private String currencyCode;

    public Product() {
        // This is needed by jackson to create json
    }

    public Product(long id, double value, String currencyCode) {
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

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
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
