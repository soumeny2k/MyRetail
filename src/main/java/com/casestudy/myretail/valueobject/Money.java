package com.casestudy.myretail.valueobject;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;
import java.util.Objects;

public class Money {
    private BigDecimal value;
    private String code;

    public Money() {
        // This is needed
    }

    public Money(BigDecimal value, String code) {
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

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Money)) {
            return false;
        }
        Money money = (Money) o;
        return Objects.equals(value, money.value) &&
                Objects.equals(code, money.code);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value, code);
    }
}
