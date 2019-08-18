package com.sample.myretail.service;

import com.sample.myretail.domain.ProductPrice;
import com.sample.myretail.exception.ProductNotFoundException;
import com.sample.myretail.repository.ProductPriceRepository;
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

    private final ProductPriceRepository productPriceRepository;
    private final RedskyService redskyService;

    /**
     * 1. Self-autowired reference to proxified bean of this class.
     */
    @Resource
    private ProductService self;

    public ProductService(ProductPriceRepository productPriceRepository,
                          RedskyService redskyService) {
        this.productPriceRepository = productPriceRepository;
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
        final ProductPrice productPrice = self.fetchProduct(productId);
        product.setCurrency(new Currency(productPrice.getCurrency().getValue(), productPrice.getCurrency().getCode()));
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
    public ProductPrice fetchProduct(long productId) {
        final Optional<ProductPrice> productOptional = productPriceRepository.findById(productId);
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
    @CachePut(value = "products", key = "#product.productId")
    public ProductPrice updateProduct(Product product) {
        final ProductPrice productPrice = self.fetchProduct(product.getProductId());
        if (productPrice == null) {
            throw new ProductNotFoundException(PRODUCT_NOT_FOUND_MSG + product.getProductId());
        }

        LOGGER.info("Saving data to MongoDB: product = {}", product.getProductId());
        productPrice.getCurrency().setValue(product.getCurrency().getValue());
        return productPriceRepository.save(productPrice);
    }
}
