
package com.example.product_microservice.controller;

import com.example.product_microservice.entity.ProductEntity;
import com.example.product_microservice.exception.GlobalExceptionHandler;
import com.example.product_microservice.exception.ProductNotFoundException;
import com.example.product_microservice.service.ProductService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ProductController.class)
@Import(GlobalExceptionHandler.class)
class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ProductService productService;

    private ProductEntity createProductEntity() {
        ProductEntity product = new ProductEntity();
        product.setId("product-123");
        product.setProductName("Teclado");
        product.setProductDescription("Teclado mecánico");
        product.setUnitPrice(100.0);
        return product;
    }

    private String validProductJson() {
        return """
            {
                "productName": "Teclado",
                "productDescription": "Teclado mecánico",
                "unitPrice": 100.0
            }
            """;
    }

    @Test
    void shouldReturnAllProducts() throws Exception {
        when(productService.findAll())
                .thenReturn(List.of(createProductEntity()));

        mockMvc.perform(get("/api/v1/products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].productName").value("Teclado"));

        verify(productService).findAll();
    }

    @Test
    void shouldReturnProductById() throws Exception {
        when(productService.findById("product-123"))
                .thenReturn(createProductEntity());

        mockMvc.perform(get("/api/v1/products/product-123"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("product-123"))
                .andExpect(jsonPath("$.productName").value("Teclado"));

        verify(productService).findById("product-123");
    }

    @Test
    void shouldReturn404WhenProductDoesNotExist() throws Exception {
        when(productService.findById("missing-id"))
                .thenThrow(new ProductNotFoundException(
                        "Producto no encontrado con id: missing-id"));

        mockMvc.perform(get("/api/v1/products/missing-id"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("Not Found"))
                .andExpect(jsonPath("$.messages[0]")
                        .value("Producto no encontrado con id: missing-id"))
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.path")
                        .value("/api/v1/products/missing-id"));
    }

    @Test
    void shouldCreateProduct() throws Exception {
        when(productService.create(any(ProductEntity.class)))
                .thenReturn(createProductEntity());

        mockMvc.perform(post("/api/v1/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validProductJson()))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value("product-123"))
                .andExpect(jsonPath("$.productName").value("Teclado"));

        verify(productService).create(any(ProductEntity.class));
    }

    @Test
    void shouldReturn400WhenProductNameIsBlank() throws Exception {
        String json = """
            {
                "productName": "   ",
                "productDescription": "Producto de prueba",
                "unitPrice": 100.0
            }
            """;

        mockMvc.perform(post("/api/v1/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.messages[0]")
                        .value("El nombre del producto es obligatorio"))
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.path")
                        .value("/api/v1/products"));

        verifyNoInteractions(productService);
    }

    @Test
    void shouldReturn400WhenProductPriceIsNegative() throws Exception {
        String json = """
            {
                "productName": "Teclado",
                "productDescription": "Teclado mecánico",
                "unitPrice": -10.0
            }
            """;

        mockMvc.perform(post("/api/v1/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.messages[0]")
                        .value("El precio unitario debe ser mayor que cero"));

        verifyNoInteractions(productService);
    }

    @Test
    void shouldReturn400WhenProductDescriptionIsBlank() throws Exception {
        String json = """
            {
                "productName": "Teclado",
                "productDescription": "   ",
                "unitPrice": 100.0
            }
            """;

        mockMvc.perform(post("/api/v1/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.messages[0]")
                        .value("La descripción del producto es obligatoria"));

        verifyNoInteractions(productService);
    }

    @Test
    void shouldReturnStructuredErrorWhenProductNameIsBlank()
            throws Exception {
        String json = """
            {
                "productName": "   ",
                "productDescription": "Producto de prueba",
                "unitPrice": 100.0
            }
            """;

        mockMvc.perform(post("/api/v1/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.messages").isArray())
                .andExpect(jsonPath("$.messages[0]")
                        .value("El nombre del producto es obligatorio"))
                .andExpect(jsonPath("$.path")
                        .value("/api/v1/products"));
    }

    @Test
    void shouldUpdateProduct() throws Exception {
        when(productService.update(
                org.mockito.ArgumentMatchers.eq("product-123"),
                any(ProductEntity.class)))
                .thenReturn(createProductEntity());

        mockMvc.perform(put("/api/v1/products/product-123")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validProductJson()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("product-123"));

        verify(productService).update(
                org.mockito.ArgumentMatchers.eq("product-123"),
                any(ProductEntity.class));
    }

    @Test
    void shouldReturn404WhenUpdatingMissingProduct() throws Exception {
        when(productService.update(
                org.mockito.ArgumentMatchers.eq("missing-id"),
                any(ProductEntity.class)))
                .thenThrow(new ProductNotFoundException(
                        "Producto no encontrado con id: missing-id"));

        mockMvc.perform(put("/api/v1/products/missing-id")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validProductJson()))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.messages[0]")
                        .value("Producto no encontrado con id: missing-id"))
                .andExpect(jsonPath("$.path")
                        .value("/api/v1/products/missing-id"));
    }

    @Test
    void shouldDeleteProduct() throws Exception {
        doNothing().when(productService).delete("product-123");

        mockMvc.perform(delete("/api/v1/products/product-123"))
                .andExpect(status().isNoContent());

        verify(productService).delete("product-123");
    }

    @Test
    void shouldReturn404WhenDeletingMissingProduct() throws Exception {
        doThrow(new ProductNotFoundException(
                "Producto no encontrado con id: missing-id"))
                .when(productService).delete("missing-id");

        mockMvc.perform(delete("/api/v1/products/missing-id"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("Not Found"))
                .andExpect(jsonPath("$.messages[0]")
                        .value("Producto no encontrado con id: missing-id"))
                .andExpect(jsonPath("$.path")
                        .value("/api/v1/products/missing-id"));
    }

    @Test
    void shouldReturn400WhenRequestBodyIsInvalidJson() throws Exception {
        mockMvc.perform(post("/api/v1/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{ invalid json }"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.messages").isArray())
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.path")
                        .value("/api/v1/products"));

        verifyNoInteractions(productService);
    }
}
