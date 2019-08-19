package com.casestudy.myretail.valueobject;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Objects;

/**
 * This is the Product details class which has all the product properties
 */
@SuppressWarnings({"PMD.VariableNamingConventions", "PMD.MethodNamingConventions"})
public class Product {
    private long id;
    private String name;
    private Money money;

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
        if (!(o instanceof Product)) {
            return false;
        }
        Product product = (Product) o;
        return id == product.id &&
                Objects.equals(name, product.name) &&
                Objects.equals(money, product.money);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, money);
    }
}
