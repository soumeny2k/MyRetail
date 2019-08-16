package com.sample.myretail;

import com.sample.myretail.repository.Product;
import com.sample.myretail.repository.ProductRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker;
import org.springframework.context.annotation.Bean;
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
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    private void populateData(ProductRepository productRepository) {

        Object[][] data = {
                {13860428, 18.87, "USD"},
                {15117729, 16.87, "USD"},
                {16483589, 33.87, "USD"},
                {16696652, 50.87, "USD"},
                {16752456, 100.345, "USD"},
                {15643793, 86.345, "USD"}
        };

        final List<Product> all = Arrays.stream(data).map(
                array -> new Product((long) array[0],
                        (double) array[1],
                        (String) array[2]))
                .collect(Collectors.toList());

        productRepository.deleteAll();
        productRepository.saveAll(all);
    }

}
