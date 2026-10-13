
package com.example.product_microservice.service;

import com.example.product_microservice.entity.ProductEntity;
import com.example.product_microservice.exception.ProductNotFoundException;
import com.example.product_microservice.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductService productService;

    private ProductEntity product;

    @BeforeEach
    void setUp() {
        product = new ProductEntity();
        product.setId("1");
        product.setProductName("Teclado");
        product.setProductDescription("Teclado mecánico");
        product.setUnitPrice(899.99);
    }

    @Test
    void findAll_shouldReturnProducts() {
        when(productRepository.findAll())
                .thenReturn(List.of(product));

        List<ProductEntity> result = productService.findAll();

        assertEquals(1, result.size());
        assertEquals("Teclado", result.get(0).getProductName());

        verify(productRepository).findAll();
    }

    @Test
    void findById_shouldReturnProductWhenItExists() {
        when(productRepository.findById("1"))
                .thenReturn(Optional.of(product));

        ProductEntity result = productService.findById("1");

        assertNotNull(result);
        assertEquals("Teclado", result.getProductName());

        verify(productRepository).findById("1");
    }

    @Test
    void findById_shouldThrowExceptionWhenProductDoesNotExist() {
        when(productRepository.findById("999"))
                .thenReturn(Optional.empty());

        ProductNotFoundException exception = assertThrows(
                ProductNotFoundException.class,
                () -> productService.findById("999")
        );

        assertEquals(
                "Producto no encontrado con id: 999",
                exception.getMessage()
        );

        verify(productRepository).findById("999");
    }

    @Test
    void create_shouldSaveProductWithoutExistingId() {
        when(productRepository.save(any(ProductEntity.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        ProductEntity input = new ProductEntity();
        input.setId("id-no-debe-conservarse");
        input.setProductName("Mouse");
        input.setProductDescription("Mouse inalámbrico");
        input.setUnitPrice(399.99);

        ProductEntity result = productService.create(input);

        assertNull(input.getId());
        assertNull(result.getId());
        assertEquals("Mouse", result.getProductName());

        verify(productRepository).save(input);
    }

    @Test
    void update_shouldSaveChangesWhenProductExists() {
        when(productRepository.findById("1"))
                .thenReturn(Optional.of(product));

        when(productRepository.save(any(ProductEntity.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        ProductEntity changes = new ProductEntity();
        changes.setProductName("Teclado RGB");
        changes.setProductDescription("Teclado actualizado");
        changes.setUnitPrice(999.99);

        ProductEntity result = productService.update("1", changes);

        assertNotNull(result);
        assertEquals("Teclado RGB", result.getProductName());
        assertEquals("Teclado actualizado",
                result.getProductDescription());
        assertEquals(999.99, result.getUnitPrice());

        verify(productRepository).findById("1");
        verify(productRepository).save(product);
    }

    @Test
    void update_shouldThrowExceptionWhenProductDoesNotExist() {
        when(productRepository.findById("999"))
                .thenReturn(Optional.empty());

        ProductEntity changes = new ProductEntity();
        changes.setProductName("Teclado RGB");

        ProductNotFoundException exception = assertThrows(
                ProductNotFoundException.class,
                () -> productService.update("999", changes)
        );

        assertEquals(
                "Producto no encontrado con id: 999",
                exception.getMessage()
        );

        verify(productRepository).findById("999");
        verify(productRepository, never())
                .save(any(ProductEntity.class));
    }

    @Test
    void delete_shouldDeleteProductWhenItExists() {
        when(productRepository.existsById("1"))
                .thenReturn(true);

        assertDoesNotThrow(() -> productService.delete("1"));

        verify(productRepository).existsById("1");
        verify(productRepository).deleteById("1");
    }

    @Test
    void delete_shouldThrowExceptionWhenProductDoesNotExist() {
        when(productRepository.existsById("999"))
                .thenReturn(false);

        ProductNotFoundException exception = assertThrows(
                ProductNotFoundException.class,
                () -> productService.delete("999")
        );

        assertEquals(
                "Producto no encontrado con id: 999",
                exception.getMessage()
        );

        verify(productRepository).existsById("999");
        verify(productRepository, never()).deleteById("999");
    }
}
