package com.casestudy.myretail.entity;

import org.springframework.data.annotation.Id;

import java.util.Objects;

/**
 * This is the pojo which represents the DB entity in MongoDB
 */
public class ProductPrice {
    @Id
    private long productId;
    private Currency currency;

    public ProductPrice() {
        // in case want to use setter
    }

    public ProductPrice(long productId, Currency currency) {
        this.productId = productId;
        this.currency = currency;
    }

    public long getProductId() {
        return productId;
    }

    public void setProductId(long productId) {
        this.productId = productId;
    }

    public Currency getCurrency() {
        return currency;
    }

    public void setCurrency(Currency currency) {
        this.currency = currency;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ProductPrice)) {
            return false;
        }
        ProductPrice productPrice = (ProductPrice) o;
        return productId == productPrice.productId &&
                Objects.equals(currency, productPrice.currency);
    }

    @Override
    public int hashCode() {
        return Objects.hash(productId, currency);
    }

    @Override
    public String toString() {
        return String.format(
                "Product[id=%s, value='%s', currencyCode='%s']",
                productId, currency.getValue(), currency.getCode());
    }
}
