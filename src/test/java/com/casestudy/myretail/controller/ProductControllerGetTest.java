package com.casestudy.myretail.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.MongoClientException;
import com.casestudy.myretail.MyRetailSpringConfigTest;
import com.casestudy.myretail.entity.Currency;
import com.casestudy.myretail.entity.ProductPrice;
import com.casestudy.myretail.repository.ProductPriceRepository;
import com.casestudy.myretail.service.RedskyService;
import com.casestudy.myretail.valueobject.Product;
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
    private ProductPriceRepository productPriceRepository;

    @MockBean
    private RedskyService redskyService;

    @Test
    public void testSuccess() throws Exception {
        final Product product = new Product();
        product.setProductId(productId);
        product.setName("Test product");

        when(redskyService.getProduct(anyLong()))
                .thenReturn(product);

        final ProductPrice productPrice = new ProductPrice();
        productPrice.setProductId(productId);
        final Currency currency = new Currency();
        currency.setValue(BigDecimal.valueOf(18.98));
        currency.setCode("USD");
        productPrice.setCurrency(currency);
        Optional<ProductPrice> optionalProduct = Optional.of(productPrice);

        when(productPriceRepository.findById(anyLong()))
                .thenReturn(optionalProduct);

        final MvcResult result = mockMvc.perform(get("/products/" + productId))
                .andExpect(status().isOk())
                .andReturn();

        final Product resultProduct = MAPPER.readValue(result.getResponse().getContentAsString(), Product.class);
        assertEquals("Product value match", 18.98, resultProduct.getMoney().getValue().doubleValue(), 0.0);
        assertEquals("Currency code match", "USD", resultProduct.getMoney().getCode());
    }

    @Test
    public void testRedskyServiceFailure() throws Exception {
        when(redskyService.getProduct(anyLong()))
                .thenThrow(new RestClientException("Service not available"));

        final ProductPrice productPrice = new ProductPrice();
        productPrice.setProductId(productId);
        final Currency currency = new Currency();
        currency.setValue(BigDecimal.valueOf(18.98));
        currency.setCode("USD");
        productPrice.setCurrency(currency);
        Optional<ProductPrice> optionalProduct = Optional.of(productPrice);

        when(productPriceRepository.findById(anyLong()))
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
        product.setProductId(productId);
        product.setName("Test product");

        when(redskyService.getProduct(anyLong()))
                .thenReturn(product);

        when(productPriceRepository.findById(anyLong()))
                .thenThrow(new MongoClientException("Mongo service is down"));

        mockMvc.perform(get("/products/" + productId))
                .andExpect(status().isInternalServerError())
                .andReturn();
    }
}
