package com.sample.myretail.service;

import com.sample.myretail.repository.Product;
import com.sample.myretail.repository.ProductRepository;
import com.sample.myretail.valueObjects.ProductDetails;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ProductService {

    private final ProductRepository productRepository;
    private final RedskyService redskyService;

    public ProductService(ProductRepository productRepository,
                          RedskyService redskyService) {
        this.productRepository = productRepository;
        this.redskyService = redskyService;
    }

    public ProductDetails getProductDetails(long productId) throws Exception {
        final ProductDetails productDetails = redskyService.getProductDetails(productId);
        if (productDetails == null) return null;

        final Optional<Product> product = productRepository.findById(productId);
        if (!product.isPresent()) return null;
        productDetails.setCurrent_price(new ProductDetails.CurrentPrice(product.get().getValue(), product.get().getCurrencyCode()));

        return productDetails;
    }

    public boolean updateProduct(ProductDetails productDetails) {
        final Optional<Product> product = productRepository.findById(productDetails.getId());

        if (product.isPresent()) {
            product.get().setValue(productDetails.getCurrent_price().getValue());
            productRepository.save(product.get());
            return true;
        }
        return false;
    }
}
