package com.example.productmanagement.controller;

import com.example.productmanagement.dto.ProductRequest;
import com.example.productmanagement.dto.ProductResponse;
import com.example.productmanagement.dto.QuantityRequest;
import com.example.productmanagement.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
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
 * <p>Each method accepts the request, validates it, delegates to
 * {@link com.example.productmanagement.service.ProductService}, and shapes the
 * response. Bodies are DTOs, never the entity. Expected failures are declared
 * per endpoint with {@code @ApiResponses} so Swagger UI shows them.
 */
@RestController
@RequestMapping
public class ProductController {

    /** Base path for every product endpoint */
    private static final String BASE_PATH = "/api/v1/products";

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping(BASE_PATH)
    @Operation(summary = "Return all products")
    @ApiResponse(responseCode = "200", description = "List of all products")
    public List<ProductResponse> getProducts() {
        return productService.listProducts();
    }

    @GetMapping(BASE_PATH + "/{id}")
    @Operation(summary = "Return product by ID")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Product found"),
        @ApiResponse(
                responseCode = "404",
                description = "No product with this id",
                content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    public ProductResponse getProductById(@PathVariable Long id) {
        return productService.getProductById(id);
    }

    @PostMapping(BASE_PATH)
    @Operation(summary = "Create a new product")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Product created"),
        @ApiResponse(
                responseCode = "400",
                description = "Request body failed validation",
                content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    public ResponseEntity<ProductResponse> createProduct(@Valid @RequestBody ProductRequest request) {
        ProductResponse created = productService.createProduct(request);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(created.id())
                .toUri();
        return ResponseEntity.created(location).body(created);
    }

    @PutMapping(BASE_PATH + "/{id}")
    @Operation(summary = "Update product by ID")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Product updated"),
        @ApiResponse(
                responseCode = "400",
                description = "Request body failed validation",
                content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
        @ApiResponse(
                responseCode = "404",
                description = "No product with this id",
                content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    public ProductResponse updateProduct(@PathVariable Long id, @Valid @RequestBody ProductRequest request) {
        return productService.updateProduct(id, request);
    }

    @DeleteMapping(BASE_PATH + "/{id}")
    @Operation(summary = "Delete product by ID")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Product deleted"),
        @ApiResponse(
                responseCode = "404",
                description = "No product with this id",
                content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
    }

    @PatchMapping(BASE_PATH + "/{id}/quantity/add")
    @Operation(summary = "Add to product stock")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Stock increased"),
        @ApiResponse(
                responseCode = "400",
                description = "Amount failed validation",
                content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
        @ApiResponse(
                responseCode = "404",
                description = "No product with this id",
                content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    public ProductResponse addQuantity(@PathVariable Long id, @Valid @RequestBody QuantityRequest request) {
        return productService.addProductQuantity(id, request.quantity());
    }

    @PatchMapping(BASE_PATH + "/{id}/quantity/subtract")
    @Operation(summary = "Subtract from product stock")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Stock decreased"),
        @ApiResponse(
                responseCode = "400",
                description = "Amount failed validation",
                content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
        @ApiResponse(
                responseCode = "404",
                description = "No product with this id",
                content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
        @ApiResponse(
                responseCode = "409",
                description = "Not enough stock available",
                content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    public ProductResponse subtractQuantity(@PathVariable Long id, @Valid @RequestBody QuantityRequest request) {
        return productService.subtractProductQuantity(id, request.quantity());
    }
}
