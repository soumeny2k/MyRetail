package com.casestudy.myretail;

import com.google.common.base.Predicates;
import com.casestudy.myretail.config.MyRetailConfig;
import com.casestudy.myretail.entity.Currency;
import com.casestudy.myretail.entity.ProductPrice;
import com.casestudy.myretail.repository.ProductPriceRepository;
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
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.client.RestTemplate;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.builders.ResponseMessageBuilder;
import springfox.documentation.schema.ModelRef;
import springfox.documentation.service.ResponseMessage;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * This is the starting point of MyRetail Spring boot application
 */
@SpringBootApplication
@EnableCircuitBreaker
@EnableCaching
@EnableSwagger2
@SuppressWarnings("PMD.ExcessiveImports")
public class MyRetailApplication {

    private static final String CURRENCY_CODE = "USD";

    public static void main(String args[]) {
        SpringApplication.run(MyRetailApplication.class, args);
    }

    @Bean
    CommandLineRunner init(ProductPriceRepository productPriceRepository) {
        return args -> populateData(productPriceRepository);
    }

    /**
     * This method will insert all the data that needs to put in MongoDB
     * @param productPriceRepository bean reference
     */
    private void populateData(ProductPriceRepository productPriceRepository) {

        Object[][] data = {
                { 13860428L, 18.87, CURRENCY_CODE },
                { 15117729L, 16.87, CURRENCY_CODE },
                { 16483589L, 33.87, CURRENCY_CODE },
                { 16696652L, 50.87, CURRENCY_CODE },
                { 16752456L, 100.34, CURRENCY_CODE },
                { 15643793L, 86.35, CURRENCY_CODE },
                { 13860423L, 200.50, CURRENCY_CODE }
        };

        final List<ProductPrice> productPrices = Arrays.stream(data)
                .map(array -> new ProductPrice(
                        (long) array[0],
                        new Currency(
                                BigDecimal.valueOf((double) array[1]),
                                (String) array[2]
                        ))
                )
                .collect(Collectors.toList());

        productPriceRepository.deleteAll();
        productPriceRepository.saveAll(productPrices);
    }

    @Bean
    public RestTemplate restTemplate(MyRetailConfig productConfig) {
        return new RestTemplate(getClientHttpRequestFactory(productConfig.getTimeout()));
    }

    /**
     * This method will create HttpRequestFactory to attach with RestTemplate
     * @param timeout timeout value for rest call
     * @return
     */
    private ClientHttpRequestFactory getClientHttpRequestFactory(int timeout) {
        final HttpComponentsClientHttpRequestFactory clientHttpRequestFactory
                = new HttpComponentsClientHttpRequestFactory();
        clientHttpRequestFactory.setConnectTimeout(timeout);
        clientHttpRequestFactory.setReadTimeout(timeout);
        return clientHttpRequestFactory;
    }

    @Bean
    public CacheManager cacheManager() {
        final SimpleCacheManager cacheManager = new SimpleCacheManager();
        cacheManager.setCaches(Arrays.asList(
                new ConcurrentMapCache("products")));
        return cacheManager;
    }

    /**
     * Enable Swagger UI
     * @return
     */
    @Bean
    public Docket api() {
        final List<ResponseMessage> responseMessageBuilders = new ArrayList<>(2);
        responseMessageBuilders.add(new ResponseMessageBuilder()
                .code(500)
                .message("500 message")
                .responseModel(new ModelRef("string"))
                .build());
        responseMessageBuilders.add(new ResponseMessageBuilder()
                .code(404)
                .message("Not Found")
                .build());

        return new Docket(DocumentationType.SWAGGER_2)
                .select()
                .apis(RequestHandlerSelectors.any())
                .paths(PathSelectors.any())
                .paths(Predicates.not(PathSelectors.regex("/error.*")))
                .build()
                .useDefaultResponseMessages(false)
                .globalResponseMessage(RequestMethod.GET, responseMessageBuilders)
                .globalResponseMessage(RequestMethod.PUT, responseMessageBuilders);
    }

}
