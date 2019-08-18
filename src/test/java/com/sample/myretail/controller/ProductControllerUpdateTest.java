package com.sample.myretail.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sample.myretail.MyRetailSpringConfigTest;
import com.sample.myretail.domain.Money;
import com.sample.myretail.domain.Price;
import com.sample.myretail.repository.ProductRepository;
import com.sample.myretail.valueobject.Currency;
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

import java.math.BigDecimal;
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

        final Product product = new Product();
        product.setId(productId);
        product.setName("Test product");
        product.setCurrency(new Currency(BigDecimal.valueOf(100.98), "USD"));

        final Price updatedPrice = new Price();
        updatedPrice.setId(productId);
        updatedPrice.setMoney(new Money(product.getCurrency().getValue(), product.getCurrency().getCode()));

        when(productRepository.save(any(Price.class)))
                .thenReturn(updatedPrice);

        final MvcResult result = mockMvc.perform(put("/products/" + productId)
                .contentType(APPLICATION_JSON)
                .content(MAPPER.writeValueAsString(product))
        )
                .andExpect(status().isOk())
                .andReturn();

        final Product resultProduct = MAPPER.readValue(result.getResponse().getContentAsString(), Product.class);
        assertEquals("Product value match", product.getCurrency().getValue().doubleValue(), resultProduct.getCurrency().getValue().doubleValue(), 0.0);
        assertEquals("Currency code match", "USD", resultProduct.getCurrency().getCode());
    }

    @Test
    public void testDoesNotMatch() throws Exception {
        final Product product = new Product();
        product.setId(123456L);
        product.setName("Test product");
        product.setCurrency(new Currency(BigDecimal.valueOf(100.98), "USD"));

        mockMvc.perform(put("/products/" + productId)
                .contentType(APPLICATION_JSON)
                .content(MAPPER.writeValueAsString(product))
        )
                .andExpect(status().isBadRequest())
                .andReturn();
    }

    @Test
    public void testProductNotFound() throws Exception {
        when(productRepository.findById(anyLong()))
                .thenReturn(Optional.empty());

        final Product product = new Product();
        product.setId(productId);
        product.setName("Test product");
        product.setCurrency(new Currency(BigDecimal.valueOf(100.98), "USD"));

        mockMvc.perform(put("/products/" + productId)
                .contentType(APPLICATION_JSON)
                .content(MAPPER.writeValueAsString(product))
        )
                .andExpect(status().isNotFound())
                .andReturn();
    }
}
