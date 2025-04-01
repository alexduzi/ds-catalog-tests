package com.devsuperior.dscatalog.tests;

import com.devsuperior.dscatalog.dto.ProductDTO;
import com.devsuperior.dscatalog.entities.Category;
import com.devsuperior.dscatalog.entities.Product;

import java.time.Instant;

public class Factory {

    public static Product createProduct(Long id) {
        if (id == null) {
            id = 1L;
        }
        Product product =  new Product(id, "Phone", "Good Phone", 800.0, "https://img.com/img.png", Instant.now());
        product.getCategories().add(new Category(2L, "Eletronics"));
        return product;
    }

    public static ProductDTO createProductDTO() {
        Product product = createProduct(null);
        return new ProductDTO(product, product.getCategories());
    }

    public static ProductDTO createProductDTO(Long id) {
        Product product = createProduct(id);
        return new ProductDTO(product, product.getCategories());
    }

    public static Category createCategory() {
        return new Category(2L, "Eletronics");
    }
}
