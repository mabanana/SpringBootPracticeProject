package com.example.productmanagement.exception;

/**
 * Signals that a product id does not exist.
 *
 * <p>Thrown by {@link com.example.productmanagement.service.ProductService} when a
 * lookup or mutation by id finds nothing. It is a domain-level statement ("no
 * such product"), so it deliberately carries no HTTP vocabulary: the web layer
 * decides the representation when the controller is wired.
 *
 * <p>Unchecked so a failed lookup inside a {@code @Transactional} method rolls
 * back rather than committing partial work.
 */
public class ProductNotFoundException extends RuntimeException {
    public ProductNotFoundException(Long id) {
        super("Product not found with id: " + id);
    }
}
