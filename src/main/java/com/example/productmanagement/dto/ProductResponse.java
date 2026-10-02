package com.example.productmanagement.dto;

import java.math.BigDecimal;

/**
 * Outbound DTO: the JSON body returned by every read endpoint.
 *
 * <p>Carries the generated {@code id} so the caller knows which row was received.
 */
public record ProductResponse(Long id, String productName, BigDecimal price, Integer quantity) {}
