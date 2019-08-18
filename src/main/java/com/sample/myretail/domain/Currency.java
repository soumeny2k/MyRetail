package com.sample.myretail.domain;

import java.math.BigDecimal;
import java.util.Objects;

public class Currency {

    private BigDecimal value;
    private String code;

    public Currency() {
        // in case want to use setter
    }

    public Currency(BigDecimal value, String code) {
        this.value= value;
        this.code = code;
    }

    public BigDecimal getValue() {
        return value;
    }

    public void setValue(BigDecimal value) {
        this.value = value;
    }

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

        if (!(o instanceof Currency)) {
            return false;
        }
        Currency currency = (Currency) o;
        return Objects.equals(value, currency.value) &&
                Objects.equals(code, currency.code);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value, code);
    }
}
