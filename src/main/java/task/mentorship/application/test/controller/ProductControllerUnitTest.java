package task.mentorship.application.test.controller;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

import task.mentorship.application.controller.ProductController;
import task.mentorship.application.dto.PaginationRequest;
import task.mentorship.application.dto.ProductRequest;
import task.mentorship.application.dto.UpdateProductRequest;
import task.mentorship.application.entity.Product;
import task.mentorship.application.service.ProductService;

import java.util.List;

@ExtendWith(MockitoExtension.class)


public class ProductControllerUnitTest {

    private MockMvc mockMvc;

    @Mock
    private ProductService productService;

    @InjectMocks
    private ProductController productController;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(productController).build();
    }

    @Test
    void testCreateProduct_Success() throws Exception {
        ProductRequest productRequest = new ProductRequest();
        productRequest.setName("Test Product");
        productRequest.setPrice(10.99);

        Product product = new Product();
        product.setId(1L);
        product.setName("Test Product");
        product.setPrice(10.99);

        when(productService.createProduct(any(ProductRequest.class))).thenReturn(product);

        mockMvc.perform(multipart("/api/products/add-product")
                        .param("name", productRequest.getName())
                        .param("price", String.valueOf(productRequest.getPrice())))
                .andExpect(status().isCreated())
                .andExpect(content().json(objectMapper.writeValueAsString(product)));
    }

    @Test
    void testDeleteProduct_Success() throws Exception {
        Long productId = 1L;
        doNothing().when(productService).deleteProduct(productId);

        mockMvc.perform(delete("/api/products/delete-product/{id}", productId))
                .andExpect(status().isCreated());
                //.andExpect(content().string("product deactivated successfully"));
    }

    @Test
    void testUpdateProduct_Success() throws Exception {
        UpdateProductRequest updateRequest = new UpdateProductRequest();
        updateRequest.setId(1L);
        updateRequest.setName("Updated Product");

        Product updatedProduct = new Product();
        updatedProduct.setId(1L);
        updatedProduct.setName("Updated Product");

        when(productService.updateProduct(any(UpdateProductRequest.class))).thenReturn(updatedProduct);

        mockMvc.perform(put("/api/products/update-product")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isCreated())
                .andExpect(content().json(objectMapper.writeValueAsString(updatedProduct)));
    }

    @Test
    void testListProducts_Success() throws Exception {
        PaginationRequest paginationRequest = new PaginationRequest();
        paginationRequest.setOffset(0);
        paginationRequest.setPageSize(10);

        Product product1 = new Product();
        product1.setId(1L);
        product1.setName("Product 1");

        Product product2 = new Product();
        product2.setId(2L);
        product2.setName("Product 2");

        Page<Product> productPage = new PageImpl<>(List.of(product1, product2));

        when(productService.listProducts(anyInt(), anyInt())).thenReturn(productPage);

        mockMvc.perform(post("/api/products/list-products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(paginationRequest)))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(productPage)));
    }

}
