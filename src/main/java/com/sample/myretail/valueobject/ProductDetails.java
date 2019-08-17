package com.sample.myretail.valueobject;

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
        private String currency_code;

        public CurrentPrice() {
            // This is needed for
        }

        public CurrentPrice(double value, String currency_code) {
            this.value = value;
            this.currency_code = currency_code;
        }

        public double getValue() {
            return value;
        }

        public String getCurrency_code() {
            return currency_code;
        }

        public void setCurrency_code(String currency_code) {
            this.currency_code = currency_code;
        }
    }
}
