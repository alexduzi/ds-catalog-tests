package com.devsuperior.dscatalog.services;

import com.devsuperior.dscatalog.dto.ProductDTO;
import com.devsuperior.dscatalog.entities.Category;
import com.devsuperior.dscatalog.entities.Product;
import com.devsuperior.dscatalog.repositories.CategoryRepository;
import com.devsuperior.dscatalog.repositories.ProductRepository;
import com.devsuperior.dscatalog.services.exceptions.DatabaseException;
import com.devsuperior.dscatalog.services.exceptions.ResourceNotFoundException;
import com.devsuperior.dscatalog.tests.Factory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
class ProductServiceTest {

    @InjectMocks
    private ProductService service;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private CategoryRepository categoryRepository;

    private long existingId;
    private long nonExistingId;
    private long dependentId;
    private PageImpl<Product> page;
    private Product product;
    private ProductDTO productDto;
    private Product nullProduct;
    private Category category;
    private long categoryId;

    @BeforeEach
    void setUp() {
        existingId = 1L;
        nonExistingId = 100L;
        dependentId = 3L;
        product = Factory.createProduct(null);
        productDto = Factory.createProductDTO();
        nullProduct = null;
        category = Factory.createCategory();
        categoryId = 2L;

        page = new PageImpl<>(List.of(product));

        when(productRepository.findAll((Pageable) any())).thenReturn(page);

        when(productRepository.save(any())).thenReturn(product);

        doNothing().when(productRepository).deleteById(existingId);
        when(productRepository.existsById(existingId)).thenReturn(true);
        when(productRepository.existsById(nonExistingId)).thenReturn(false);
        when(productRepository.existsById(dependentId)).thenReturn(true);
        doThrow(DataIntegrityViolationException.class).when(productRepository).deleteById(dependentId);

        when(productRepository.findById(existingId)).thenReturn(Optional.of(product));
        when(productRepository.findById(nonExistingId)).thenReturn(Optional.ofNullable(nullProduct));

        when(productRepository.getReferenceById(existingId)).thenReturn(product);
        when(categoryRepository.getReferenceById(existingId)).thenReturn(category);
        when(productRepository.save(product)).thenReturn(product);

        doThrow(ResourceNotFoundException.class).when(productRepository).getReferenceById(nonExistingId);
    }

    @Test
    void updateShouldThrowExceptionWhenEntityNotExists() {
        assertThrows(ResourceNotFoundException.class, () -> service.update(nonExistingId, productDto));

        verify(productRepository, times(1)).getReferenceById(nonExistingId);
    }

    @Test
    void updateShouldReturnWhenEntityExists() {
        ProductDTO dto = service.update(existingId, productDto);

        assertNotNull(dto);
        assertInstanceOf(ProductDTO.class, dto);
        verify(productRepository, times(1)).getReferenceById(existingId);
        verify(categoryRepository, times(1)).getReferenceById(categoryId);
    }

    @Test
    void findByIdShouldReturnExceptionWhenEntityDoesNotExists() {
        assertThrows(ResourceNotFoundException.class, () -> service.findById(nonExistingId));
        verify(productRepository, times(1)).findById(nonExistingId);
    }

    @Test
    void findByIdShouldReturnEntity() {
        ProductDTO dto = service.findById(existingId);

        assertNotNull(dto);
        assertInstanceOf(ProductDTO.class, dto);
        verify(productRepository, times(1)).findById(existingId);
    }

    @Test
    void deleteShouldDoNothingWhenIdExists() {
        assertDoesNotThrow(() -> service.delete(existingId));

        verify(productRepository, times(1)).deleteById(existingId);
    }

    @Test
    void findAllPagedShouldReturnPage() {
        Pageable pageable = PageRequest.of(0, 10);

        Page<ProductDTO> result = service.findAllPaged(pageable);

        assertNotNull(result);
        verify(productRepository).findAll(pageable);
    }

    @Test
    void deleteShouldThrowResourceNotFoundExceptionWhenIdDoesNotExist() {
        assertThrows(ResourceNotFoundException.class, () -> service.delete(nonExistingId));
    }

    @Test
    void deleteShouldThrowDatabaseExceptionWhenDependentId() {
        assertThrows(DatabaseException.class, () -> service.delete(dependentId));

        verify(productRepository, times(1)).deleteById(dependentId);
    }
}