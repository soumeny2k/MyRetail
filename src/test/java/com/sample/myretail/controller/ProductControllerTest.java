package com.sample.myretail.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.MongoClientException;
import com.sample.myretail.MyRetailSpringConfigTest;
import com.sample.myretail.repository.Product;
import com.sample.myretail.repository.ProductRepository;
import com.sample.myretail.service.RedskyService;
import com.sample.myretail.valueObjects.ProductDetails;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.web.client.RestClientException;

import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebMvcTest(ProductController.class)
@Import(MyRetailSpringConfigTest.class)
public class ProductControllerTest {

    private static final ObjectMapper MAPPER = new ObjectMapper();
    private long productId = 16696652L;

    @Autowired
    private MockMvc mockMvc;

    @MockBean(name = "productRepository")
    private ProductRepository productRepository;

    @MockBean
    private RedskyService redskyService;

    @Test
    public void testSuccess() throws Exception {
        final ProductDetails productDetails = new ProductDetails();
        productDetails.setId(productId);
        productDetails.setName("Test product");

        when(redskyService.getProductDetails(anyLong()))
                .thenReturn(productDetails);

        final Product product = new Product();
        product.setId(productId);
        product.setValue(18.98);
        product.setCurrencyCode("USD");
        Optional<Product> optionalProduct = Optional.of(product);

        when(productRepository.findById(anyLong()))
                .thenReturn(optionalProduct);

        final MvcResult result = mockMvc.perform(get("/products/" + productId))
                .andExpect(status().isOk())
                .andReturn();

        final ProductDetails resultProduct = MAPPER.readValue(result.getResponse().getContentAsString(), ProductDetails.class);
        assertEquals("Product value match", 18.98, resultProduct.getCurrent_price().getValue(), 0.0);
        assertEquals("Currency code match", "USD", resultProduct.getCurrent_price().getCurrencyCode());
    }

    @Test
    public void testRedskyServiceFailure() throws Exception {
        when(redskyService.getProductDetails(anyLong()))
                .thenThrow(new RestClientException("Service not available"));

        final Product product = new Product();
        product.setId(productId);
        product.setValue(18.98);
        product.setCurrencyCode("USD");
        Optional<Product> optionalProduct = Optional.of(product);

        when(productRepository.findById(anyLong()))
                .thenReturn(optionalProduct);

        mockMvc.perform(get("/products/" + productId))
                .andExpect(status().isInternalServerError())
                .andReturn();
    }

    @Test
    public void testRedskyServiceProductNotFound() throws Exception {
        when(redskyService.getProductDetails(anyLong()))
                .thenThrow(new RestClientException("Service not available"));

        mockMvc.perform(get("/products/" + productId))
                .andExpect(status().isInternalServerError())
                .andReturn();
    }

    @Test
    public void testMongoServiceIsDown() throws Exception {
        final ProductDetails productDetails = new ProductDetails();
        productDetails.setId(productId);
        productDetails.setName("Test product");

        when(redskyService.getProductDetails(anyLong()))
                .thenReturn(productDetails);

        when(productRepository.findById(anyLong()))
                .thenThrow(new MongoClientException("Mongo service is down"));

        mockMvc.perform(get("/products/" + productId))
                .andExpect(status().isInternalServerError())
                .andReturn();
    }
}
