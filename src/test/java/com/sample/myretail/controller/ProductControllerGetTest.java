package com.sample.myretail.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.MongoClientException;
import com.sample.myretail.MyRetailSpringConfigTest;
import com.sample.myretail.domain.Money;
import com.sample.myretail.domain.Price;
import com.sample.myretail.repository.ProductRepository;
import com.sample.myretail.service.RedskyService;
import com.sample.myretail.valueobject.Product;
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

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebMvcTest(ProductController.class)
@Import(MyRetailSpringConfigTest.class)
public class ProductControllerGetTest {

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
        final Product product = new Product();
        product.setId(productId);
        product.setName("Test product");

        when(redskyService.getProduct(anyLong()))
                .thenReturn(product);

        final Price price = new Price();
        price.setId(productId);
        final Money money = new Money();
        money.setValue(BigDecimal.valueOf(18.98));
        money.setCurrencyCode("USD");
        price.setMoney(money);
        Optional<Price> optionalProduct = Optional.of(price);

        when(productRepository.findById(anyLong()))
                .thenReturn(optionalProduct);

        final MvcResult result = mockMvc.perform(get("/products/" + productId))
                .andExpect(status().isOk())
                .andReturn();

        final Product resultProduct = MAPPER.readValue(result.getResponse().getContentAsString(), Product.class);
        assertEquals("Product value match", 18.98, resultProduct.getCurrency().getValue().doubleValue(), 0.0);
        assertEquals("Currency code match", "USD", resultProduct.getCurrency().getCode());
    }

    @Test
    public void testRedskyServiceFailure() throws Exception {
        when(redskyService.getProduct(anyLong()))
                .thenThrow(new RestClientException("Service not available"));

        final Price price = new Price();
        price.setId(productId);
        price.setId(productId);
        final Money money = new Money();
        money.setValue(BigDecimal.valueOf(18.98));
        money.setCurrencyCode("USD");
        price.setMoney(money);
        Optional<Price> optionalProduct = Optional.of(price);

        when(productRepository.findById(anyLong()))
                .thenReturn(optionalProduct);

        mockMvc.perform(get("/products/" + productId))
                .andExpect(status().isInternalServerError())
                .andReturn();
    }

    @Test
    public void testRedskyServiceProductNotFound() throws Exception {
        when(redskyService.getProduct(anyLong()))
                .thenThrow(new RestClientException("Service not available"));

        mockMvc.perform(get("/products/" + productId))
                .andExpect(status().isInternalServerError())
                .andReturn();
    }

    @Test
    public void testMongoServiceIsDown() throws Exception {
        final Product product = new Product();
        product.setId(productId);
        product.setName("Test product");

        when(redskyService.getProduct(anyLong()))
                .thenReturn(product);

        when(productRepository.findById(anyLong()))
                .thenThrow(new MongoClientException("Mongo service is down"));

        mockMvc.perform(get("/products/" + productId))
                .andExpect(status().isInternalServerError())
                .andReturn();
    }
}
