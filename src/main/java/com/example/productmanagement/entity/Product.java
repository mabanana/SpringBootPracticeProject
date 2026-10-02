package com.example.productmanagement.entity;

import com.example.productmanagement.exception.InsufficientQuantityException;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.math.BigDecimal;

/**
 * JPA entity representing one row in the {@code product} table.
 *
 * <p>Annotations sit on the fields, so Hibernate uses field access and never calls
 * the accessors. That is what lets the setters stay protected: callers change
 * stock through {@link #addQuantity(int)} and {@link #removeQuantity(int)}, which
 * enforce the invariants. There is no {@code setId} for the same reason a mutable
 * identity would let a caller turn an insert into a merge.
 *
 * <p>No validation annotations here: input rules belong on the request DTO,
 * which guards the API edge. What is enforced here are domain invariants, which
 * hold for every writer — the API, seed data, and any future service.
 */
@Entity
@Table(name = "product")
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String productName;
    private BigDecimal price;
    private int quantity;

    public Product() {}

    public Product(String productName, BigDecimal price, int quantity) {
        setProductName(productName);
        setPrice(price);
        setQuantity(quantity);
    }

    public Long getId() {
        return id;
    }

    public String getProductName() {
        return productName;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public int getQuantity() {
        return quantity;
    }

    // Protected setters, all mutations routed through here
    protected void setProductName(String productName) {
        if (productName == null || productName.isBlank()) {
            throw new IllegalArgumentException("Product name cannot be null or blank");
        }
        this.productName = productName;
    }

    protected void setPrice(BigDecimal price) {
        if (price == null || price.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Price cannot be null or negative");
        }
        this.price = price;
    }

    protected void setQuantity(int quantity) {
        if (quantity < 0) {
            throw new IllegalArgumentException("Quantity cannot be negative");
        }
        this.quantity = quantity;
    }

    // Business logic methods to modify the quantity of the product.
    public int addQuantity(int amount) {
        if (amount < 0) {
            throw new IllegalArgumentException("Amount to add cannot be negative");
        }
        setQuantity(this.quantity + amount);
        return this.quantity;
    }

    public int removeQuantity(int amount) {
        if (amount < 0) {
            throw new IllegalArgumentException("Amount to remove cannot be negative");
        }
        if (this.quantity - amount < 0) {
            throw new InsufficientQuantityException(this.quantity, amount);
        }
        setQuantity(this.quantity - amount);
        return this.quantity;
    }

    // Method to update the product's fields from plain values.
    // The service maps the DTO to these parameters, so the entity never
    // depends on the API layer.
    public void updateFrom(String productName, BigDecimal price, int quantity) {
        setProductName(productName);
        setPrice(price);
        setQuantity(quantity);
    }
}
