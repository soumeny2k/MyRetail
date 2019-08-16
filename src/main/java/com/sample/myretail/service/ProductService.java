package com.sample.myretail.service;

import com.sample.myretail.repository.Product;
import com.sample.myretail.repository.ProductRepository;
import com.sample.myretail.valueobjects.ProductDetails;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Optional;

@Service
public class ProductService {
    private static final Logger LOGGER = LoggerFactory.getLogger(ProductService.class);

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

    public ProductDetails getProductDetails(long productId) throws Exception {
        // Fetch data from redsky service
        final ProductDetails productDetails = redskyService.getProductDetails(productId);
        if (productDetails == null) {
            return null;
        }

        // Fetch data from Mongo
        final Product product = self.fetchProduct(productId);
        productDetails.setCurrentPrice(new ProductDetails.CurrentPrice(product.getValue(), product.getCurrencyCode()));

        return productDetails;
    }

    @Cacheable(value = "products")
    public Product fetchProduct(long productId) {
        LOGGER.info("Retrieving data from MongoDB");
        final Optional<Product> productOptional = productRepository.findById(productId);
        if (!productOptional.isPresent()) {
            return null;
        }

        return productOptional.get();
    }

    @CachePut(value = "products", key = "#productDetails.id")
    public Product updateProduct(ProductDetails productDetails) {
        final Product product = self.fetchProduct(productDetails.getId());

        if (product != null) {
            LOGGER.info("Saving data to MongoDB");
            product.setValue(productDetails.getCurrentPrice().getValue());
            return productRepository.save(product);
        }
        return null;
    }
}
