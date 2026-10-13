package com.example.product_microservice.entity;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(value = "product")
@Setter
@Getter
@NoArgsConstructor
public class ProductEntity {

    @Id
    private String id;

    @NotBlank(message = "El nombre del producto es obligatorio")
    @Size(max = 100, message = "El nombre no debe superar los 100 caracteres")
    private String productName;

    @NotBlank(message = "La descripción del producto es obligatoria")
    @Size(max = 500, message = "La descripción no debe superar los 500 caracteres")
    private String productDescription;

    @NotNull(message = "El precio unitario es obligatorio")
    @DecimalMin(value = "0.0", inclusive = false,
            message = "El precio unitario debe ser mayor que cero")
    private Double unitPrice;
}
