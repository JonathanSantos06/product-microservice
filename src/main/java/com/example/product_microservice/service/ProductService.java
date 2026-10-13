
package com.example.product_microservice.service;

import com.example.product_microservice.entity.ProductEntity;
import com.example.product_microservice.exception.ProductNotFoundException;
import com.example.product_microservice.repository.ProductRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductService {

    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    // Obtener todos los productos
    public List<ProductEntity> findAll() {
        return productRepository.findAll();
    }

    // Obtener un producto por ID
    public ProductEntity findById(String id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException(
                        "Producto no encontrado con id: " + id));
    }

    // Crear un producto
    public ProductEntity create(ProductEntity product) {
        product.setId(null);
        return productRepository.save(product);
    }

    // Actualizar un producto existente
    public ProductEntity update(
            String id,
            ProductEntity productDetails) {

        ProductEntity existingProduct = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException(
                        "Producto no encontrado con id: " + id));

        existingProduct.setProductName(productDetails.getProductName());
        existingProduct.setProductDescription(
                productDetails.getProductDescription());
        existingProduct.setUnitPrice(productDetails.getUnitPrice());

        return productRepository.save(existingProduct);
    }

    // Eliminar un producto
    public void delete(String id) {
        if (!productRepository.existsById(id)) {
            throw new ProductNotFoundException(
                    "Producto no encontrado con id: " + id);
        }

        productRepository.deleteById(id);
    }
}
