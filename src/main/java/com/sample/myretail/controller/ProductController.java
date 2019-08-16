package com.sample.myretail.controller;

import com.sample.myretail.repository.Product;
import com.sample.myretail.service.ProductService;
import com.sample.myretail.valueobject.ProductDetails;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static org.springframework.http.HttpStatus.*;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

/**
 * Controller class for Product, which has all the product related Rest services
 */
@RestController
@RequestMapping(value = "/products")
public class ProductController {

    private static final Logger LOGGER = LoggerFactory.getLogger(ProductService.class);
    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    /**
     * This service will take productId as path variable and  return the product details for that id
     *
     * @param id product id
     * @return If found then details of the specified product with HTTP Status OK
     *         If the product not found then it will return HTTP Status NOT_FOUND
     *         If any error occurred then it will return HTTP Status INTERNAL_SERVER_ERROR
     */
    @GetMapping(value = "/{id}", produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<ProductDetails> get(@PathVariable Long id) {
        try {
            final ProductDetails productDetails = productService.getProductDetails(id);
            if (productDetails == null) {
                return new ResponseEntity<>(NOT_FOUND);
            }

            return ResponseEntity.ok(productDetails);
        } catch (Exception ex) {
            LOGGER.error("Error fetching product details:", ex);
            return new ResponseEntity<>(INTERNAL_SERVER_ERROR);
        }
    }

    /**
     *  This service will take productId and product details and update the product rice in DB
     *
     * @param id product id
     * @param productDetails product details
     * @return If updated then details of the updated product with HTTP Status OK
     *         If the product not found then it will return HTTP Status NOT_FOUND
     *         If any error occurred then it will return HTTP Status INTERNAL_SERVER_ERROR
     */
    @PutMapping(value = "/{id}", produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<ProductDetails> update(@PathVariable Long id, @RequestBody ProductDetails productDetails) {
        try {
            if (id != productDetails.getId()) {
                return new ResponseEntity<>(BAD_REQUEST);
            }
            final Product product = productService.updateProduct(productDetails);
            if (product == null) {
                return new ResponseEntity<>(NOT_FOUND);
            }

            productDetails.setCurrent_price(new ProductDetails.CurrentPrice(product.getValue(), product.getCurrencyCode()));
            return ResponseEntity.ok(productDetails);
        } catch (Exception ex) {
            LOGGER.error("Error updating product price:", ex);
            return new ResponseEntity<>(INTERNAL_SERVER_ERROR);
        }
    }
}
