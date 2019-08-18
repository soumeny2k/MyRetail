package com.sample.myretail.service;

import com.sample.myretail.domain.Price;
import com.sample.myretail.exception.ProductNotFoundException;
import com.sample.myretail.repository.ProductRepository;
import com.sample.myretail.valueobject.Currency;
import com.sample.myretail.valueobject.Product;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Optional;

/**
 * This class represents the service for product operation
 */
@Service
public class ProductService {
    private static final Logger LOGGER = LoggerFactory.getLogger(ProductService.class);
    private static final String PRODUCT_NOT_FOUND_MSG = "Product not found: product = ";

    private final ProductRepository productRepository;
    private final RedskyService redskyService;

    /**
     * 1. Self-autowired reference to proxified bean of this class.
     */
    @Resource
    private ProductService self;

    public ProductService(ProductRepository productRepository,
                          RedskyService redskyService) {
        this.productRepository = productRepository;
        this.redskyService = redskyService;
    }

    /**
     * This method will call another service to get the product details from external source.
     * If product found then it will fetch the product price from MongoDB and merge it with
     * ProductDetails.
     *
     * @param productId product id
     * @return product id, name, price, currency code
     * @throws Exception
     */
    public Product getProduct(long productId) throws Exception {
        // Fetch data from redsky service
        final Product product = redskyService.getProduct(productId);
        if (product == null) {
            throw new ProductNotFoundException(PRODUCT_NOT_FOUND_MSG + productId);
        }

        // Fetch data from Mongo
        final Price price = self.fetchProduct(productId);
        product.setCurrency(new Currency(price.getMoney().getValue(), price.getMoney().getCurrencyCode()));
        return product;
    }

    /**
     * This method fetch product data from MongoDB.
     * It will also cache the data, so that new requests will serve from cache
     * rather than going to DB.
     *
     * @param productId product id
     * @return product details
     */
    @Cacheable(value = "products")
    public Price fetchProduct(long productId) {
        final Optional<Price> productOptional = productRepository.findById(productId);
        if (!productOptional.isPresent()) {
            throw new ProductNotFoundException(PRODUCT_NOT_FOUND_MSG + productId);
        }
        LOGGER.info("Data found in MongoDB: product = {}", productId);
        return productOptional.get();
    }

    /**
     * This method will update the product details in MongoDB
     * as well as in the cache.
     *
     * @param product product details
     * @return Updated produce details
     */
    @CachePut(value = "products", key = "#product.id")
    public Price updateProduct(Product product) {
        final Price price = self.fetchProduct(product.getId());
        if (price == null) {
            throw new ProductNotFoundException(PRODUCT_NOT_FOUND_MSG + product.getId());
        }

        LOGGER.info("Saving data to MongoDB: product = {}", product.getId());
        price.getMoney().setValue(product.getCurrency().getValue());
        return productRepository.save(price);
    }
}
