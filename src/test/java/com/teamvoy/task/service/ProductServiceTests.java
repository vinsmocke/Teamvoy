package com.teamvoy.task.service;

import com.teamvoy.task.exception.EntityNotFoundException;
import com.teamvoy.task.exception.NullEntityReferenceException;
import com.teamvoy.task.model.Product;
import com.teamvoy.task.repository.ProductRepository;
import com.teamvoy.task.service.impl.ProductServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class ProductServiceTests {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductServiceImpl productService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreateProductSuccess() {
        Product product = new Product(1L, "Laptop", 10, 1000);

        when(productRepository.existsByName(product.getName())).thenReturn(false);
        when(productRepository.save(product)).thenReturn(product);

        Product createdProduct = productService.create(product);

        assertNotNull(createdProduct);
        assertEquals(product, createdProduct);

        verify(productRepository, times(1)).existsByName(product.getName());
        verify(productRepository, times(1)).save(product);
    }

    @Test
    void testCreateProductAlreadyExists() {
        Product existingProduct = new Product(1L, "Laptop", 5, 10000);
        Product newProduct = new Product(2L, "Laptop", 10, 15000);

        when(productRepository.existsByName(existingProduct.getName())).thenReturn(true);
        when(productRepository.findByNameIgnoreCase("Laptop")).thenReturn(Optional.of(existingProduct));
        when(productRepository.save(any(Product.class))).thenReturn(existingProduct);

        Product updatedProduct = productService.create(newProduct);

        assertNotNull(updatedProduct);
        assertEquals(existingProduct.getAmount(), updatedProduct.getAmount());

        verify(productRepository, times(1)).existsByName(existingProduct.getName());
        verify(productRepository, times(1)).findByNameIgnoreCase("Laptop");
        verify(productRepository, times(1)).save(existingProduct);
    }

    @Test
    void testCreateProductWithNullProduct() {
        assertThrows(NullEntityReferenceException.class, () -> productService.create(null));

        verifyNoInteractions(productRepository);
    }

    @Test
    void testReadProductByIdSuccess() {
        Product product = new Product(1L, "Laptop", 10, 10000);
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        Product retrievedProduct = productService.readById(1L);

        assertNotNull(retrievedProduct);
        assertEquals(product, retrievedProduct);

        verify(productRepository, times(1)).findById(1L);
    }

    @Test
    void testReadProductByIdNotFound() {
        when(productRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> productService.readById(1L));

        verify(productRepository, times(1)).findById(1L);
    }

    @Test
    void testUpdateProductSuccess() {
        Product existingProduct = new Product(1L, "Laptop", 10, 10000);
        Product updatedProduct = new Product(1L, "Laptop", 15, 20000);
        when(productRepository.findById(1L)).thenReturn(Optional.of(existingProduct));
        when(productRepository.save(updatedProduct)).thenReturn(updatedProduct);

        Product modifiedProduct = productService.update(updatedProduct);

        assertNotNull(modifiedProduct);
        assertEquals(updatedProduct, modifiedProduct);

        verify(productRepository, times(1)).findById(1L);
        verify(productRepository, times(1)).save(updatedProduct);
    }

    @Test
    void testUpdateProductNotFound() {
        Product product = new Product(1L, "Laptop", 10, 10000);
        when(productRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> productService.update(product));

        verify(productRepository, times(1)).findById(1L);
        verifyNoMoreInteractions(productRepository);
    }

    @Test
    void testUpdateProductWithNullProduct() {
        assertThrows(NullEntityReferenceException.class, () -> productService.update(null));

        verifyNoInteractions(productRepository);
    }

    @Test
    void testDeleteProductSuccess() {
        Product product = new Product(1L, "Laptop", 10, 1000);
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        productService.delete(1L);

        verify(productRepository, times(1)).findById(1L);
        verify(productRepository, times(1)).delete(product);
    }

    @Test
    void testDeleteProductNotFound() {
        when(productRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> productService.delete(1L));

        verify(productRepository, times(1)).findById(1L);
        verifyNoMoreInteractions(productRepository);
    }

    @Test
    void testGetAllProducts() {
        List<Product> productList = new ArrayList<>();
        productList.add(new Product(1L, "Laptop", 10, 10000));
        productList.add(new Product(2L, "Mouse", 20, 20000));
        when(productRepository.findAll()).thenReturn(productList);

        List<Product> allProducts = productService.getAll();

        assertNotNull(allProducts);
        assertEquals(productList.size(), allProducts.size());

        verify(productRepository, times(1)).findAll();
    }
}
