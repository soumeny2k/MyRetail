package com.sample.myretail.repository;

import com.sample.myretail.domain.Price;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * This class is the repository class for Product.
 * This class has all CRUD method to handle MongoDB related operations.
 */
public interface ProductRepository extends MongoRepository<Price, Long> { }
