package com.bonniego.repository;

import com.bonniego.model.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    // List<Product> findByNameContainingIgnoreCase(String keyword);
    Page<Product> findByCategoryAndBrand(String category, String brand, Pageable pageable);
}
