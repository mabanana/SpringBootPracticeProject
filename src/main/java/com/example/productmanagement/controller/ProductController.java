package com.example.productmanagement.controller;

import com.example.productmanagement.dto.ProductRequest;
import com.example.productmanagement.dto.ProductResponse;
import com.example.productmanagement.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

/**
 * REST boundary for products: the only place in the app that knows about HTTP.
 *
 * <p>Each method does four things and nothing more — accept the request, validate
 * it, delegate to {@link com.example.productmanagement.service.ProductService},
 * shape the HTTP response. No business rules and no database access belong here;
 * if you find yourself writing an {@code if} about product state, it belongs in
 * the service.
 *
 * <p>Request and response types are DTOs, never the entity. Bodies arrive as
 * {@link ProductRequest} and responses go out as {@link ProductResponse}, so the
 * {@code product} table stays an implementation detail rather than part of the
 * published contract.
 *
 * <p>{@code @Operation} summaries are not decoration: springdoc reads them into
 * the OpenAPI document, which is what Swagger UI displays. Without them the
 * endpoints appear in the UI as unexplained paths.
 */
@RestController
@RequestMapping
public class ProductController {

    /**
     * Base path for every product endpoint, versioned from the start.
     *
     * <p>{@code static final} is not optional here: annotation attribute values
     * must be compile-time constants, so a mutable or non-final field produces
     * "element value must be a constant expression" at compile time.
     */
    private static final String SLUG = "/api/v1/products";

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping(SLUG)
    @Operation(summary = "Return all products")
    public List<ProductResponse> getProducts() {
        return productService.listProducts();
    }

    @GetMapping(SLUG + "/{id}")
    @Operation(summary = "Return product by ID")
    public ProductResponse getProductById(@PathVariable Long id) {
        return productService.getProductById(id);
    }

    @PostMapping(SLUG)
    @Operation(summary = "Create a new product")
    public ResponseEntity<ProductResponse> createProduct(@Valid @RequestBody ProductRequest request) {
        ProductResponse created = productService.createProduct(request);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(created.id())
                .toUri();
        return ResponseEntity.created(location).body(created);
    }

    @PutMapping(SLUG + "/{id}")
    @Operation(summary = "Update product by ID")
    public ProductResponse updateProduct(@PathVariable Long id, @Valid @RequestBody ProductRequest request) {
        return productService.updateProduct(id, request);
    }

    @DeleteMapping(SLUG + "/{id}")
    @Operation(summary = "Delete product by ID")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
    }
}
