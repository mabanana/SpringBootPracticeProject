package com.example.productmanagement.repository;

import com.example.productmanagement.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Persistence boundary for {@link Product}.
 *
 * <p>This interface is the whole data-access layer. Spring Data generates the
 * implementation at startup, so there is no class to write and no SQL to write:
 * extending {@link JpaRepository} already supplies the necessary CRUD operations.
 */
public interface ProductRepository extends JpaRepository<Product, Long> {}
