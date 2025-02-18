package task.mentorship.application.test.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

import jakarta.persistence.EntityNotFoundException;
import task.mentorship.application.dto.ProductRequest;
import task.mentorship.application.dto.UpdateProductRequest;
import task.mentorship.application.entity.Category;
import task.mentorship.application.entity.Product;
import task.mentorship.application.entity.Unit;
import task.mentorship.application.repository.CategoryRepository;
import task.mentorship.application.repository.ProductRepository;
import task.mentorship.application.service.ProductService;

import java.io.IOException;
import java.util.*;

@ExtendWith(MockitoExtension.class)
public class ProductServiceUnitTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private ProductService productService;

    private Product product;
    private Category category;

    @BeforeEach
    void setUp() {
        category = new Category();
        category.setId(1L);
        category.setName("pieces");

        product = new Product();
        product.setId(1L);
        product.setName("orange");
        product.setUnit(Unit.PIECE);
        product.setPrice(120.00);
        product.setDescription("nice");
        product.setActive(true);
        product.setCategories(new HashSet<>(List.of(category)));
        product.setImage(new byte[]{});
    }

    // ✅ Test Product Creation
    @Test
    void testCreateProduct_Success() throws IOException {
        ProductRequest request = new ProductRequest();
        request.setName("orange");
        request.setUnit("PIECE");
        request.setPrice(120.00);
        request.setDescription("High-end laptop");
        request.setCategoryIds(List.of(1L));
        //request.setImage(() -> new byte[]{});

        when(categoryRepository.findAllById(request.getCategoryIds())).thenReturn(List.of(category));
        when(productRepository.save(any(Product.class))).thenReturn(product);

        Product createdProduct = productService.createProduct(request);

        assertNotNull(createdProduct);
        assertEquals(request.getName(), createdProduct.getName());
        assertEquals(request.getUnit(), createdProduct.getUnit().name());
        assertEquals(request.getPrice(), createdProduct.getPrice());
    }

    @Test
    void testDeleteProduct_Success() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        productService.deleteProduct(1L);

        assertFalse(product.getActive());
        verify(productRepository, times(1)).save(product);
    }

    @Test
    void testDeleteProduct_ProductNotFound() {
        when(productRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> productService.deleteProduct(1L));
    }

    @Test
    void testUpdateProduct_Success() {
        UpdateProductRequest request = new UpdateProductRequest();
        request.setId(1L);
        request.setName("Updated orange");
        request.setPrice(1300.00);
        request.setActive(true);
        request.setDescription("Updated description");

        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(productRepository.save(any(Product.class))).thenReturn(product);

        Product updatedProduct = productService.updateProduct(request);

        assertEquals(request.getName(), updatedProduct.getName());
        assertEquals(request.getPrice(), updatedProduct.getPrice());
        assertEquals(request.getDescription(), updatedProduct.getDescription());
    }

    @Test
    void testUpdateProduct_ProductNotFound() {
        UpdateProductRequest request = new UpdateProductRequest();
        request.setId(2L);
        request.setName("Updated orange");

        when(productRepository.findById(2L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> productService.updateProduct(request));
    }

    @Test
    void testListProducts_Success() {
        Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.ASC, "id"));
        Page<Product> productPage = new PageImpl<>(List.of(product));

        when(productRepository.findAll(pageable)).thenReturn(productPage);

        Page<Product> retrievedProducts = productService.listProducts(0, 10);

        assertEquals(1, retrievedProducts.getContent().size());
        assertEquals("orange", retrievedProducts.getContent().get(0).getName());
    }

}
