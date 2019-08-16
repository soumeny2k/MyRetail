package com.sample.myretail;

import com.sample.myretail.config.ProductConfig;
import com.sample.myretail.repository.Product;
import com.sample.myretail.repository.ProductRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
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
public class MyRetailApplication {

    public static void main(String args[]) {
        SpringApplication.run(MyRetailApplication.class, args);
    }

    @Bean
    CommandLineRunner init(ProductRepository productRepository) {
        return args -> populateData(productRepository);
    }

    @Bean
    public RestTemplate restTemplate(ProductConfig productConfig) {
        return new RestTemplate(getClientHttpRequestFactory(productConfig.getTimeout()));
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

    private ClientHttpRequestFactory getClientHttpRequestFactory(int timeout) {
        HttpComponentsClientHttpRequestFactory clientHttpRequestFactory
                = new HttpComponentsClientHttpRequestFactory();
        clientHttpRequestFactory.setConnectTimeout(timeout);
        return clientHttpRequestFactory;
    }

}
