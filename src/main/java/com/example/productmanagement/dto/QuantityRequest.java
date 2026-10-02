package com.example.productmanagement.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

/**
 * Inbound DTO: the stock amount for the add/subtract quantity endpoints.
 *
 * <p>Zero is a harmless no-op; negatives are rejected here so they never reach
 * the entity guards.
 */
public record QuantityRequest(@NotNull @PositiveOrZero Integer quantity) {}
