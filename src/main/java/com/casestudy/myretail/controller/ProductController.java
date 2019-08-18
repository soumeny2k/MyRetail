package com.casestudy.myretail.controller;

import com.casestudy.myretail.valueobject.Product;
import com.casestudy.myretail.entity.ProductPrice;
import com.casestudy.myretail.exception.ProductNotFoundException;
import com.casestudy.myretail.service.ProductService;
import com.casestudy.myretail.valueobject.Money;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;

import static org.springframework.http.HttpStatus.*;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

/**
 * Controller class for Product, which has all the product related Rest services
 */
@RestController
@RequestMapping(value = "/products")
@SuppressWarnings({ "PMD.PreserveStackTrace", "PMD.ConfusingTernary"})
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
    public ResponseEntity<Product> get(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(productService.getProduct(id));
        } catch (ProductNotFoundException pnfe) {
            LOGGER.error(pnfe.getMessage(), pnfe);
            throw new ResponseStatusException(NOT_FOUND, pnfe.getMessage());
        } catch (Exception ex) {
            final String msg = "Error fetching product details: product = " + id;
            LOGGER.error(msg, ex);
            throw new ResponseStatusException(INTERNAL_SERVER_ERROR, msg);
        }
    }

    /**
     *  This service will take productId and product details and update the product rice in DB
     *
     * @param id product id
     * @param product product details
     * @return If updated then details of the updated product with HTTP Status OK
     *         If the product not found then it will return HTTP Status NOT_FOUND
     *         If any error occurred then it will return HTTP Status INTERNAL_SERVER_ERROR
     */
    @PutMapping(value = "/{id}", produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<Product> update(@PathVariable Long id, @RequestBody Product product) {
        validate(id, product);
        try {
            final ProductPrice productPrice = productService.updateProduct(product);
            // update product price with latest value
            product.setMoney(new Money(productPrice.getCurrency().getValue(), productPrice.getCurrency().getCode()));
            return ResponseEntity.ok(product);
        } catch (ProductNotFoundException pnfe) {
            LOGGER.error(pnfe.getMessage(), pnfe);
            throw new ResponseStatusException(NOT_FOUND, pnfe.getMessage());
        } catch (Exception ex) {
            final String msg = "Error updating product price: product = " + id;
            LOGGER.error(msg, ex);
            throw new ResponseStatusException(INTERNAL_SERVER_ERROR, msg);
        }
    }

    /**
     * This method will validate the input request
     * @param id product id
     * @param product product details
     */
    private void validate(Long id, Product product) {
        boolean isValid = true;
        String msg = null;
        if (id != product.getProductId()) {
            msg = "Product id does not match";
            isValid = false;
        } else if (product.getMoney().getValue().compareTo(BigDecimal.ZERO) < 0) {
            msg = "Product price should be greater than 0";
            isValid = false;
        }
        if (!isValid) {
            throw new ResponseStatusException(BAD_REQUEST, msg);
        }
    }
}
