package com.sample.myretail.domain;

import java.math.BigDecimal;

public class Money {

    private BigDecimal value;
    private String currencyCode;

    public Money() {
        // in case want to use setter
    }

    public Money(BigDecimal value, String currencyCode) {
        this.value= value;
        this.currencyCode = currencyCode;
    }

    public BigDecimal getValue() {
        return value;
    }

    public void setValue(BigDecimal value) {
        this.value = value;
    }

    public String getCurrencyCode() {
        return currencyCode;
    }

    public void setCurrencyCode(String currencyCode) {
        this.currencyCode = currencyCode;
    }
}
