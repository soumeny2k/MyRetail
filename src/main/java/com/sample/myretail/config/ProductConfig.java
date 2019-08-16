package com.sample.myretail.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties("myretail.product")
public class ProductConfig {
    private String url;
    private int timeout;

    public String getUrl(long productId) {
        return String.format(url, productId);
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int getTimeout() {
        return timeout;
    }

    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }
}
