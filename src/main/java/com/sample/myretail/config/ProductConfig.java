package com.sample.myretail.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties("myRetail.product")
public class ProductConfig {
    private String url;

    public String getUrl(long productId) {
        return String.format(url, productId);
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
