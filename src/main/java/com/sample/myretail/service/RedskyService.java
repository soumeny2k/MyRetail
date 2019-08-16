package com.sample.myretail.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.sample.myretail.config.MyRetailConfig;
import com.sample.myretail.valueobjects.ProductDetails;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;

/**
 * This class has methods to call RedSky service
 */
@Service
public class RedskyService {
    private static final Logger LOGGER = LoggerFactory.getLogger(RedskyService.class);

    private final RestTemplate restTemplate;
    private final MyRetailConfig productConfig;
    private static final ObjectMapper MAPPER = new ObjectMapper();

    public RedskyService(RestTemplate restTemplate,
                         MyRetailConfig productConfig) {
        this.restTemplate = restTemplate;
        this.productConfig = productConfig;
    }

    /**
     * This method will call Redsky service to get the product details.
     * This method use circuit breaker not to block all operation in case of
     * Redsky service is down.
     *
     * @param productId product id
     * @return product details
     * @throws IOException
     */
    @HystrixCommand(fallbackMethod = "defaultProductDetails")
    public ProductDetails getProductDetails(long productId) throws IOException {
        final String redSkyServiceUrl = productConfig.getUrl(productId);
        LOGGER.info("Redsky Service url = {}", redSkyServiceUrl);
        final ResponseEntity<String> productData = restTemplate.getForEntity(redSkyServiceUrl, String.class);
        if (productData.getBody() == null) {
            return null;
        }

        LOGGER.info("Data found at Redsky: product = {}", productId);
        final JsonNode node = MAPPER.readTree(productData.getBody());
        final JsonNode product = node.get("product");
        if (!product.hasNonNull("item")) {
            return null;
        }

        final JsonNode item = product.get("item");
        if (!item.hasNonNull("tcin")) {
            return null;
        }
        final ProductDetails productDetails = new ProductDetails();
        productDetails.setId(item.get("tcin").asLong());

        final JsonNode productDescription = item.get("product_description");
        productDetails.setName(productDescription.get("title").asText());

        return productDetails;
    }

    /**
     * This is the fallback method for <code>getProductDetails</code> circuit breaker
     *
     * @param productId product id
     * @return
     */
    public ProductDetails defaultProductDetails(long productId) {
        LOGGER.error("Fetching product details from Redsky service failed, falling back to hystrix default: product={}", productId);
        return null;
    }
}
