package com.example.product_microservice.integration;

import com.example.product_microservice.repository.ProductRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import com.jayway.jsonpath.JsonPath;

@SpringBootTest
@AutoConfigureMockMvc
class ProductIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ProductRepository productRepository;

    private final List<String> createdIds = new ArrayList<>();

    @AfterEach
    void cleanUp() {
        createdIds.forEach(productRepository::deleteById);
        createdIds.clear();
    }

    @Test
    void shouldCreateAndRetrieveProduct() throws Exception {
        String name = uniqueName();

        String id = createProduct(name);

        mockMvc.perform(get("/api/v1/products/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.productName").value(name))
                .andExpect(jsonPath("$.unitPrice").value(899.99));

        assertTrue(productRepository.findById(id).isPresent());
    }

    @Test
    void shouldReturnProductsFromMongoDB() throws Exception {
        String name = uniqueName();
        createProduct(name);

        mockMvc.perform(get("/api/v1/products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[?(@.productName == '" + name + "')]")
                        .isNotEmpty());
    }

    @Test
    void shouldUpdateExistingProduct() throws Exception {
        String id = createProduct(uniqueName());

        mockMvc.perform(put("/api/v1/products/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "productName": "Producto actualizado",
                                  "productDescription": "Descripción actualizada",
                                  "unitPrice": 1299.99
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.productName")
                        .value("Producto actualizado"))
                .andExpect(jsonPath("$.unitPrice").value(1299.99));

        var saved = productRepository.findById(id).orElseThrow();

        assertEquals("Producto actualizado", saved.getProductName());
        assertEquals(1299.99, saved.getUnitPrice());
    }

    @Test
    void shouldDeleteProduct() throws Exception {
        String id = createProduct(uniqueName());

        mockMvc.perform(delete("/api/v1/products/{id}", id))
                .andExpect(status().isNoContent());

        assertFalse(productRepository.findById(id).isPresent());

        // Ya se eliminó: no hace falta borrarlo otra vez en la limpieza.
        createdIds.remove(id);
    }

    @Test
    void shouldReturn404WhenProductDoesNotExist() throws Exception {
        mockMvc.perform(get(
                        "/api/v1/products/{id}",
                        "000000000000000000000000"))
                .andExpect(status().isNotFound());
    }

    private String createProduct(String name) throws Exception {
        MvcResult result = mockMvc.perform(
                        post("/api/v1/products")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                        {
                                          "productName": "%s",
                                          "productDescription": "Producto de integración",
                                          "unitPrice": 899.99
                                        }
                                        """.formatted(name)))
                .andExpect(status().isCreated())
                .andReturn();

        String id = JsonPath.read(
                result.getResponse().getContentAsString(),
                "$.id");

        createdIds.add(id);
        return id;
    }

    private String uniqueName() {
        return "IT-" + UUID.randomUUID();
    }
}
