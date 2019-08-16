package com.sample.myretail;

import com.sample.myretail.config.ProductConfig;
import com.sample.myretail.repository.Product;
import com.sample.myretail.repository.ProductRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCache;
import org.springframework.cache.support.SimpleCacheManager;
import org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker;
import org.springframework.context.annotation.Bean;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@SpringBootApplication
@EnableCircuitBreaker
@EnableCaching
public class MyRetailApplication {

    public static void main(String args[]) {
        SpringApplication.run(MyRetailApplication.class, args);
    }

    @Bean
    CommandLineRunner init(ProductRepository productRepository) {
        return args -> populateData(productRepository);
    }

    private void populateData(ProductRepository productRepository) {

        Object[][] data = {
                {13860428L, 18.87, "USD"},
                {15117729L, 16.87, "USD"},
                {16483589L, 33.87, "USD"},
                {16696652L, 50.87, "USD"},
                {16752456L, 100.345, "USD"},
                {15643793L, 86.345, "USD"}
        };

        final List<Product> all = Arrays.stream(data).map(
                array -> new Product((long) array[0],
                        (double) array[1],
                        (String) array[2]))
                .collect(Collectors.toList());

        productRepository.deleteAll();
        productRepository.saveAll(all);
    }

    @Bean
    public RestTemplate restTemplate(ProductConfig productConfig) {
        return new RestTemplate(getClientHttpRequestFactory(productConfig.getTimeout()));
    }

    private ClientHttpRequestFactory getClientHttpRequestFactory(int timeout) {
        final HttpComponentsClientHttpRequestFactory clientHttpRequestFactory
                = new HttpComponentsClientHttpRequestFactory();
        clientHttpRequestFactory.setConnectTimeout(timeout);
        return clientHttpRequestFactory;
    }

    @Bean
    public CacheManager cacheManager() {
        final SimpleCacheManager cacheManager = new SimpleCacheManager();
        cacheManager.setCaches(Arrays.asList(
                new ConcurrentMapCache("products")));
        return cacheManager;
    }

}
