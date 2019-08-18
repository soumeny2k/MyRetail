package com.sample.myretail.valueobject;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;

public class Currency {
    private BigDecimal value;
    private String code;

    public Currency() {
        // This is needed
    }

    public Currency(BigDecimal value, String code) {
        this.value = value;
        this.code = code;
    }

    public BigDecimal getValue() {
        return value;
    }

    public void setValue(BigDecimal value) {
        this.value = value;
    }

    @JsonProperty("currency_code")
    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
