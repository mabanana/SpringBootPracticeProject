package com.example.productmanagement.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import java.math.BigDecimal;

/**
 * Inbound DTO: the JSON body accepted by create and update requests.
 *
 * <p>No {@code id} component — the path variable supplies it, so callers cannot
 * set a generated identity from the body.
 */
public record ProductRequest(
        @NotBlank String productName,
        @NotNull @PositiveOrZero BigDecimal price,
        @NotNull @PositiveOrZero Integer quantity) {}
