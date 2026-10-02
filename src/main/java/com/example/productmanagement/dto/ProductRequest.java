package com.example.productmanagement.dto;

import java.math.BigDecimal;

/**
 * Inbound DTO: the JSON body accepted by create and update requests.
 *
 * <p>No {@code id} component — the path variable supplies it, so callers cannot
 * set a generated identity from the body.
 */
public record ProductRequest(String productName, BigDecimal price, Integer quantity) {}
