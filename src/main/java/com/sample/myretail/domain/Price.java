package com.sample.myretail.domain;

import org.springframework.data.annotation.Id;

import java.util.Objects;

/**
 * This is the pojo which represents the DB entity in MongoDB
 */
public class Price {
    @Id
    private long id;
    private Money money;

    public Price() {
        // in case want to use setter
    }

    public Price(long id, Money money) {
        this.id = id;
        this.money = money;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Money getMoney() {
        return money;
    }

    public void setMoney(Money money) {
        this.money = money;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Price)) {
            return false;
        }
        Price price = (Price) o;
        return id == price.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return String.format(
                "Product[id=%s, value='%s', currencyCode='%s']",
                id, money.getValue(), money.getCurrencyCode());
    }
}
