package com.devsuperior.dscatalog.repositories;

import com.devsuperior.dscatalog.entities.Product;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;

import static com.devsuperior.dscatalog.tests.Factory.createProduct;
import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class ProductRepositoryTest {

    @Autowired
    private ProductRepository productRepository;

    private long existingId;

    private long existingId2;

    private long countTotalProducts;

    private long inexistingId;

    @BeforeEach
    void setUp() {
        existingId = 1L;
        countTotalProducts = 25L;
        existingId2 = 10L;
        inexistingId = 125;
    }

    @Test
    void saveShouldPersistWithAutoIncrementWhenIdIsNull() {
        Product product = createProduct(null);
        product.setId(null);

        product = productRepository.save(product);

        assertNotNull(product.getId());
        assertEquals(countTotalProducts + 1, product.getId());
    }

    @Test
    void deleteShouldDeleteObjectWhenIdExists() {
        productRepository.deleteById(existingId);

        Optional<Product> result = productRepository.findById(existingId);

        assertFalse(result.isPresent());
    }

    @Test
    void findByIdShouldReturnExistingProduct() {
        Optional<Product> result = productRepository.findById(existingId2);

        assertTrue(result.isPresent());
    }

    @Test
    void findByIdShouldReturnInexistingProduct() {
        Optional<Product> result = productRepository.findById(inexistingId);

        assertFalse(result.isPresent());
    }
}