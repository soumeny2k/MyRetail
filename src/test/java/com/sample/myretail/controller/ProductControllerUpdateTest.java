package com.sample.myretail.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.MongoClientException;
import com.sample.myretail.MyRetailSpringConfigTest;
import com.sample.myretail.repository.Product;
import com.sample.myretail.repository.ProductRepository;
import com.sample.myretail.valueobject.ProductDetails;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebMvcTest(ProductController.class)
@Import(MyRetailSpringConfigTest.class)
public class ProductControllerUpdateTest {

    private static final ObjectMapper MAPPER = new ObjectMapper();
    private long productId = 16696652L;

    @Autowired
    private MockMvc mockMvc;

    @MockBean(name = "productRepository")
    private ProductRepository productRepository;

    @Test
    public void testSuccess() throws Exception {
        final Product product = new Product();
        product.setId(productId);
        product.setValue(18.98);
        product.setCurrencyCode("USD");
        Optional<Product> optionalProduct = Optional.of(product);

        when(productRepository.findById(anyLong()))
                .thenReturn(optionalProduct);

        final ProductDetails productDetails = new ProductDetails();
        productDetails.setId(productId);
        productDetails.setName("Test product");
        productDetails.setCurrent_price(new ProductDetails.CurrentPrice(100.98, "USD"));

        final Product updatedProduct = new Product();
        updatedProduct.setId(productId);
        updatedProduct.setValue(productDetails.getCurrent_price().getValue());
        updatedProduct.setCurrencyCode("USD");

        when(productRepository.save(any(Product.class)))
                .thenReturn(updatedProduct);

        final MvcResult result = mockMvc.perform(put("/products/" + productId)
                .contentType(APPLICATION_JSON)
                .content(MAPPER.writeValueAsString(productDetails))
        )
                .andExpect(status().isOk())
                .andReturn();

        final ProductDetails resultProduct = MAPPER.readValue(result.getResponse().getContentAsString(), ProductDetails.class);
        assertEquals("Product value match", productDetails.getCurrent_price().getValue(), resultProduct.getCurrent_price().getValue(), 0.0);
        assertEquals("Currency code match", "USD", resultProduct.getCurrent_price().getCurrency_code());
    }

    @Test
    public void testDoesNotMatch() throws Exception {
        final ProductDetails productDetails = new ProductDetails();
        productDetails.setId(123456L);
        productDetails.setName("Test product");
        productDetails.setCurrent_price(new ProductDetails.CurrentPrice(100.98, "USD"));

        mockMvc.perform(put("/products/" + productId)
                .contentType(APPLICATION_JSON)
                .content(MAPPER.writeValueAsString(productDetails))
        )
                .andExpect(status().isBadRequest())
                .andReturn();
    }

    @Test
    public void testProductNotFound() throws Exception {
        when(productRepository.findById(anyLong()))
                .thenReturn(Optional.empty());

        final ProductDetails productDetails = new ProductDetails();
        productDetails.setId(productId);
        productDetails.setName("Test product");
        productDetails.setCurrent_price(new ProductDetails.CurrentPrice(100.98, "USD"));

        mockMvc.perform(put("/products/" + productId)
                .contentType(APPLICATION_JSON)
                .content(MAPPER.writeValueAsString(productDetails))
        )
                .andExpect(status().isNotFound())
                .andReturn();
    }
}
