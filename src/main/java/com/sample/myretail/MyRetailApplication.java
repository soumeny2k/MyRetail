package com.sample.myretail;

import com.sample.myretail.repository.Product;
import com.sample.myretail.repository.ProductRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
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
        Product product = new Product();
        product.setId(13860428);
        product.setValue(18.87);
        product.setCurrencyCode("USD");

        productRepository.save(product);

        product = new Product();
        product.setId(15117729);
        product.setValue(16.87);
        product.setCurrencyCode("USD");

        productRepository.save(product);

        product = new Product();
        product.setId(16483589);
        product.setValue(33.87);
        product.setCurrencyCode("USD");

        productRepository.save(product);

        product = new Product();
        product.setId(16696652);
        product.setValue(50.87);
        product.setCurrencyCode("USD");

        productRepository.save(product);

        product = new Product();
        product.setId(16752456);
        product.setValue(100.345);
        product.setCurrencyCode("USD");

        product = new Product();
        product.setId(15643793);
        product.setValue(86.345);
        product.setCurrencyCode("USD");

        productRepository.save(product);
    }

}
