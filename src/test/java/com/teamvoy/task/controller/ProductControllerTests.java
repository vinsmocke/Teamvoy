package com.teamvoy.task.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.teamvoy.task.dto.goodsDto.GoodsRequest;
import com.teamvoy.task.model.Product;
import com.teamvoy.task.service.ProductService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class ProductControllerTests {

    private MockMvc mockMvc;

    @Mock
    private ProductService productService;

    @InjectMocks
    private ProductController productController;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(productController).build();
    }

    @Test
    public void testGetAll() throws Exception {
        List<Product> productList = Arrays.asList(new Product(), new Product());
        when(productService.getAll()).thenReturn(productList);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/goods"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.length()").value(productList.size()));

        verify(productService, times(1)).getAll();
    }

    @Test
    public void testGetById() throws Exception {
        long productId = 1L;
        Product product = new Product();
        when(productService.readById(productId)).thenReturn(product);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/goods/{id}", productId))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(product.getId()));

        verify(productService, times(1)).readById(productId);
    }

    @Test
    public void testCreate() throws Exception {
        GoodsRequest goodsRequest = new GoodsRequest();
        Product createdProduct = new Product();
        when(productService.create(any(Product.class))).thenReturn(createdProduct);

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("/api/goods")
                        .content(asJsonString(goodsRequest))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andReturn();

        String content = result.getResponse().getContentAsString();
        Product returnedProduct = new ObjectMapper().readValue(content, Product.class);

        verify(productService, times(1)).create(any(Product.class));
    }

    @Test
    public void testUpdate() throws Exception {
        long productId = 1L;
        GoodsRequest goodsRequest = new GoodsRequest();
        Product existingProduct = new Product();
        Product updatedProduct = new Product();
        when(productService.readById(productId)).thenReturn(existingProduct);
        when(productService.update(any(Product.class))).thenReturn(updatedProduct);

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.put("/api/goods")
                        .param("id", String.valueOf(productId))
                        .content(asJsonString(goodsRequest))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isAccepted())
                .andReturn();

        String content = result.getResponse().getContentAsString();
        Product returnedProduct = new ObjectMapper().readValue(content, Product.class);

        verify(productService, times(1)).readById(productId);
        verify(productService, times(1)).update(any(Product.class));
    }

    @Test
    public void testDelete() throws Exception {
        long productId = 1L;

        mockMvc.perform(MockMvcRequestBuilders.delete("/api/goods/{id}", productId))
                .andExpect(MockMvcResultMatchers.status().isAccepted())
                .andExpect(MockMvcResultMatchers.content().string("Goods with id " + productId + " has been removed"));

        verify(productService, times(1)).delete(productId);
    }

    private String asJsonString(Object obj) {
        try {
            return new ObjectMapper().writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
