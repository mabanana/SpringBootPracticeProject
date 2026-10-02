package com.example.productmanagement.service;

import com.example.productmanagement.dto.ProductRequest;
import com.example.productmanagement.dto.ProductResponse;
import com.example.productmanagement.entity.Product;
import com.example.productmanagement.exception.ProductNotFoundException;
import com.example.productmanagement.repository.ProductRepository;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Application layer: DTO/entity mapping, not-found handling, and transaction boundaries.
 *
 * <p>Each mutating method is one transaction via {@code @Transactional}; reads are
 * {@code readOnly}. No HTTP concepts here — status codes and bodies belong to the
 * controller.
 */
@Service
public class ProductService {
    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    private ProductResponse toResponse(Product product) {
        return new ProductResponse(
                product.getId(), product.getProductName(), product.getPrice(), product.getQuantity());
    }

    // Create
    @Transactional
    public ProductResponse createProduct(ProductRequest request) {
        Product product = new Product(request.productName(), request.price(), request.quantity());
        Product savedProduct = productRepository.save(product);
        return toResponse(savedProduct);
    }

    // Read + ReadAll
    @Transactional(readOnly = true)
    public ProductResponse getProductById(Long id) {
        return productRepository.findById(id).map(this::toResponse).orElseThrow(() -> new ProductNotFoundException(id));
    }

    @Transactional(readOnly = true)
    public List<ProductResponse> listProducts() {
        return productRepository.findAll().stream().map(this::toResponse).collect(Collectors.toList());
    }

    // Update
    @Transactional
    public ProductResponse updateProduct(Long id, ProductRequest request) {
        Product product = productRepository.findById(id).orElseThrow(() -> new ProductNotFoundException(id));
        product.updateFrom(request.productName(), request.price(), request.quantity());
        Product updatedProduct = productRepository.save(product);
        return toResponse(updatedProduct);
    }

    @Transactional
    public ProductResponse addProductQuantity(Long id, int quantity) {
        Product product = productRepository.findById(id).orElseThrow(() -> new ProductNotFoundException(id));
        product.addQuantity(quantity);
        Product updatedProduct = productRepository.save(product);
        return toResponse(updatedProduct);
    }

    @Transactional
    public ProductResponse subtractProductQuantity(Long id, int quantity) {
        Product product = productRepository.findById(id).orElseThrow(() -> new ProductNotFoundException(id));
        product.removeQuantity(quantity);
        Product updatedProduct = productRepository.save(product);
        return toResponse(updatedProduct);
    }

    // Delete the managed entity to prevent race conditions.
    @Transactional
    public void deleteProduct(Long id) {
        Product product = productRepository.findById(id).orElseThrow(() -> new ProductNotFoundException(id));
        productRepository.delete(product);
    }
}
