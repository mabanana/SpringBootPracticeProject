package com.example.productmanagement.exception;

/**
 * Signals that a stock removal was refused because not enough quantity is available.
 *
 * <p>Thrown by {@link com.example.productmanagement.entity.Product#removeQuantity(int)}
 * when the requested amount exceeds current stock. It is a domain-level statement
 * ("not enough stock"), so it deliberately carries no HTTP vocabulary: the web
 * layer decides the representation, e.g. a 409 Conflict or 422 Unprocessable
 * Entity via {@code @RestControllerAdvice}.
 *
 * <p>Unchecked so a failed removal rolls back an enclosing {@code @Transactional}
 * method, and so callers that pre-check stock are not forced to catch it.
 */
public class InsufficientQuantityException extends RuntimeException {

    public InsufficientQuantityException(String message) {
        super(message);
    }

    public InsufficientQuantityException(int available, int requested) {
        super("Insufficient quantity: requested " + requested + " but only " + available + " available");
    }
}
