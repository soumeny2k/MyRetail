package com.sample.myretail.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.sample.myretail.config.ProductConfig;
import com.sample.myretail.exception.ProductException;
import com.sample.myretail.valueObjects.ProductDetails;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;

@Service
public class RedskyService {

    private static final Logger logger = LoggerFactory.getLogger(RedskyService.class);

    private final RestTemplate restTemplate;
    private final ProductConfig productConfig;
    private static final ObjectMapper MAPPER = new ObjectMapper();

    public RedskyService(RestTemplate restTemplate,
                          ProductConfig productConfig) {
        this.restTemplate = restTemplate;
        this.productConfig = productConfig;
    }

    @HystrixCommand(fallbackMethod = "defaultProductDetails")
    public ProductDetails getProductDetails(long productId) throws IOException {

        ProductDetails productDetails;
        try {
            final ResponseEntity<String> productData = restTemplate.getForEntity(productConfig.getUrl(productId), String.class);
            if (productData.getBody() == null) return null;

            productDetails = new ProductDetails();
            final JsonNode node = MAPPER.readTree(productData.getBody());
            final JsonNode product = node.get("product");
            if (!product.hasNonNull("item")) return null;

            final JsonNode item = product.get("item");
            if (!item.hasNonNull("tcin")) return null;
            productDetails.setId(item.get("tcin").asLong());

            final JsonNode productDescription = item.get("product_description");
            productDetails.setName(productDescription.get("title").asText());
        } catch(RestClientResponseException re) {
            logger.error("Error while looking for product", re);
            throw new ProductException(re.getRawStatusCode(), "Product not found");
        }
        return productDetails;
    }

    private ProductDetails defaultProductDetails() {
        return null;
    }
}
