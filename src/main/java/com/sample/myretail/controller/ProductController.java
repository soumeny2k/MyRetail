package com.sample.myretail.controller;

import com.sample.myretail.repository.Product;
import com.sample.myretail.service.ProductService;
import com.sample.myretail.valueObjects.ProductDetails;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static org.springframework.http.HttpStatus.*;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping(value = "/products")
public class ProductController {

    private static final Logger logger = LoggerFactory.getLogger(ProductService.class);

    @Autowired
    private ProductService productService;

    @GetMapping(value = "/{id}", produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<ProductDetails> get(@PathVariable Long id) {
        try {
            final ProductDetails productDetails = productService.getProductDetails(id);
            if (productDetails == null) return new ResponseEntity<>(NOT_FOUND);

            return ResponseEntity.ok(productDetails);
        } catch (Exception ex) {
            logger.error("Error fetching product details:", ex);
            return new ResponseEntity<>(INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping(value = "/{id}", produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<ProductDetails> update(@PathVariable Long id, @RequestBody ProductDetails productDetails) {
        try {
            if (id != productDetails.getId()) {
                return new ResponseEntity<>(BAD_REQUEST);
            }
            final Product product = productService.updateProduct(productDetails);
            if (product == null) return new ResponseEntity<>(NOT_FOUND);

            productDetails.setCurrent_price(new ProductDetails.CurrentPrice(product.getValue(), product.getCurrencyCode()));
            return ResponseEntity.ok(productDetails);
        } catch (Exception e) {
            logger.error("Error updating product price:", e);
            return new ResponseEntity<>(INTERNAL_SERVER_ERROR);
        }
    }
}
