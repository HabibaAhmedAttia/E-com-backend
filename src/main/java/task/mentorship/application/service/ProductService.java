package task.mentorship.application.service;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import jakarta.persistence.EntityNotFoundException;
import task.mentorship.application.dto.PaginationRequest;
import task.mentorship.application.dto.ProductRequest;
import task.mentorship.application.dto.UpdateProductRequest;
import task.mentorship.application.entity.Category;
import task.mentorship.application.entity.Product;
import task.mentorship.application.entity.Unit;
import task.mentorship.application.repository.CategoryRepository;
import task.mentorship.application.repository.ProductRepository;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

@Service

public class ProductService {
	
	@Autowired
    private ProductRepository productRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    public Product createProduct(ProductRequest productRequest) throws IOException {
        Product product = new Product();
        product.setName(productRequest.getName());
        product.setUnit(Unit.valueOf(productRequest.getUnit()));
        product.setPrice(productRequest.getPrice());
        product.setDescription(productRequest.getDescription());

        if (!productRequest.getCategoryIds().isEmpty()) {
            List<Category> categories = categoryRepository.findAllById(productRequest.getCategoryIds());
            product.setCategories(new HashSet<>(categories));
        }

        product.setImage(productRequest.getImage().getBytes());
        return productRepository.save(product);
    }

    public void deleteProduct(Long productId) {
        Product product = productRepository.findById(productId)
            .orElseThrow(() -> new EntityNotFoundException("Product not found"));
        product.setActive(false);
        productRepository.save(product);
    }
    
    public Product updateProduct(UpdateProductRequest request) {
        Optional<Product> optionalProduct = productRepository.findById(request.getId());
        if (optionalProduct.isEmpty()) {
            throw new RuntimeException("Product not found with ID: " + request.getId());
        }
        Product product = optionalProduct.get();
        product.setName(request.getName());
        product.setPrice(request.getPrice());
        product.setActive(request.getActive());
        product.setDescription(request.getDescription());

        return productRepository.save(product);
    }

    
    public Page<Product> listProducts(int offset, int pageSize) {
        Pageable pageable = PageRequest.of(offset, pageSize, Sort.by(Sort.Direction.ASC, "id"));
        return productRepository.findAll(pageable);
    }
    
   
    
}
