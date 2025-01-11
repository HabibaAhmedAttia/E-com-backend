package task.mentorship.application.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import task.mentorship.application.dto.PaginationRequest;
import task.mentorship.application.dto.ProductRequest;
import task.mentorship.application.dto.UpdateProductRequest;
import task.mentorship.application.entity.Product;
import task.mentorship.application.service.ProductService;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/products")


public class ProductController {
	

	@Autowired
    private ProductService productService;

    @PostMapping("/add-product")
    public ResponseEntity<String> createProduct(@ModelAttribute ProductRequest productRequest) throws IOException {
        Product product = productService.createProduct(productRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body("product added successfully");
    }

    @DeleteMapping("/delete-product/{id}")
    public ResponseEntity<String> deleteProduct(@PathVariable Long id) {
       productService.deleteProduct(id);
       return ResponseEntity.status(HttpStatus.CREATED).body("product deactivated successfully");
    }
	
    @PutMapping("/update-product")
    public ResponseEntity<String> updateProduct(@Validated @RequestBody UpdateProductRequest request) {
        Product updatedProduct = productService.updateProduct(request);
        return ResponseEntity.ok("product updated successfully");
    }
	
    @PostMapping("/list-products")
    public ResponseEntity<List<Product>> listProducts(@Validated @RequestBody PaginationRequest paginationRequest) {
        List<Product> products = productService.listProducts(
                paginationRequest.getOffset(),
                paginationRequest.getPageSize()
        );
        return ResponseEntity.ok(products);
    }
    
}
