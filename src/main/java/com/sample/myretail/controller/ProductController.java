package com.sample.myretail.controller;

import com.sample.myretail.exception.ProductException;
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
        if (id == null) return new ResponseEntity<>(BAD_REQUEST);
        try {
            final ProductDetails productDetails = productService.getProductDetails(id);
            if (productDetails == null) return new ResponseEntity<>(NOT_FOUND);
            return ResponseEntity.ok(productDetails);
        } catch (ProductException pe) {
            logger.error("Fetching product details from Redsky failed:", pe);
            if (pe.getStatus() == NOT_FOUND.value()) {
                return new ResponseEntity<>(NOT_FOUND);
            } else {
                return new ResponseEntity<>(INTERNAL_SERVER_ERROR);
            }
        } catch (Exception e) {
            logger.error("Error fetching product data:", e);
            return new ResponseEntity<>(INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping(value = "/{id}", produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<String> update(@RequestBody ProductDetails productDetails) {
        try {
            final boolean isUpdated = productService.updateProduct(productDetails);
            if (!isUpdated) return new ResponseEntity<>(NOT_FOUND);

            return ResponseEntity.ok("success");
        } catch (Exception e) {
            logger.error("Error updating product price:", e);
            return new ResponseEntity<>(INTERNAL_SERVER_ERROR);
        }
    }
}
