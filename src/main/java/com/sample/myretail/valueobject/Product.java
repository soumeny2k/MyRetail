package com.sample.myretail.valueobject;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Objects;

/**
 * This is the Product details class which has all the product properties
 */
@SuppressWarnings({"PMD.VariableNamingConventions", "PMD.MethodNamingConventions"})
public class Product {
    private long productId;
    private String name;
    private Money money;

    public long getProductId() {
        return productId;
    }

    public void setProductId(long productId) {
        this.productId = productId;
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
        return productId == product.productId &&
                Objects.equals(name, product.name) &&
                Objects.equals(money, product.money);
    }

    @Override
    public int hashCode() {
        return Objects.hash(productId, name, money);
    }
}
