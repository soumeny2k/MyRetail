package com.sample.myretail.valueobjects;

/**
 * This is the Product details class which has all the product properties
 */
@SuppressWarnings({"PMD.VariableNamingConventions", "PMD.MethodNamingConventions"})
public class ProductDetails {
    private long id;
    private String name;
    private CurrentPrice current_price;

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

    public CurrentPrice getCurrent_price() {
        return current_price;
    }

    public void setCurrent_price(CurrentPrice current_price) {
        this.current_price = current_price;
    }

    public static class CurrentPrice {
        private double value;
        private String currencyCode;

        public CurrentPrice() {
            // This is needed for
        }

        public CurrentPrice(double value, String currencyCode) {
            this.value = value;
            this.currencyCode = currencyCode;
        }

        public double getValue() {
            return value;
        }

        public String getCurrencyCode() {
            return currencyCode;
        }

    }
}
