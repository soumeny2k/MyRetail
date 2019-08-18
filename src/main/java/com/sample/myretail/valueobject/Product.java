package com.sample.myretail.valueobject;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * This is the Product details class which has all the product properties
 */
@SuppressWarnings({"PMD.VariableNamingConventions", "PMD.MethodNamingConventions"})
public class Product {
    private long id;
    private String name;
    private Currency currency;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @JsonProperty("current_price")
    public Currency getCurrency() {
        return currency;
    }

    public void setCurrency(Currency currency) {
        this.currency = currency;
    }
}
