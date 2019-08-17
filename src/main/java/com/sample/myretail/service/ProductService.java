package com.sample.myretail.service;

import com.sample.myretail.exception.ProductNotFoundException;
import com.sample.myretail.repository.Product;
import com.sample.myretail.repository.ProductRepository;
import com.sample.myretail.valueobject.ProductDetails;
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
    public ProductDetails getProduct(long productId) throws Exception {
        // Fetch data from redsky service
        final ProductDetails productDetails = redskyService.getProduct(productId);
        if (productDetails == null) {
            throw new ProductNotFoundException(PRODUCT_NOT_FOUND_MSG + productId);
        }

        // Fetch data from Mongo
        final Product product = self.fetchProduct(productId);
        productDetails.setCurrent_price(new ProductDetails.CurrentPrice(product.getValue(), product.getCurrencyCode()));
        return productDetails;
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
    public Product fetchProduct(long productId) {
        final Optional<Product> productOptional = productRepository.findById(productId);
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
     * @param productDetails product details
     * @return Updated produce details
     */
    @CachePut(value = "products", key = "#productDetails.id")
    public Product updateProduct(ProductDetails productDetails) {
        final Product product = self.fetchProduct(productDetails.getId());
        if (product == null) {
            throw new ProductNotFoundException(PRODUCT_NOT_FOUND_MSG + productDetails.getId());
        }

        LOGGER.info("Saving data to MongoDB: product = {}", productDetails.getId());
        product.setValue(productDetails.getCurrent_price().getValue());
        return productRepository.save(product);
    }
}
