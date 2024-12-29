package com.bonniego.service;

import com.bonniego.model.entity.Product;
import com.bonniego.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductService {
    @Autowired
    private ProductRepository productRepository;

    // 列出所有商品
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    public Product getProductById(Long id){
        return productRepository.findById(id).get();
    }

    //篩選與分頁邏輯
    public Page<Product> getFilteredProducts(String category, String brand, String sortOption, int page, int size) {
        Pageable pageable;
        if ("price-desc".equals(sortOption)) {
            pageable = PageRequest.of(page - 1, size, Sort.by("price").descending());
        } else {
            pageable = PageRequest.of(page - 1, size, Sort.by("price").ascending());
        }

        if ("all".equals(category) && "all".equals(brand)) {
            return productRepository.findAll(pageable);
        }

        // 添加篩選邏輯
        return productRepository.findByCategoryAndBrand(category, brand, pageable);
    }

    // 用keyword查找商品
//    public List<Product> searchProductsByName(String keyword) {
//        if (keyword == null || keyword.trim().isEmpty()) {
//            throw new IllegalArgumentException("Keyword cannot be null or empty");
//        }
//        return productRepository.findByNameContainingIgnoreCase(keyword);
//    }

    //新增商品
    public Product addProduct(Product product) {
        return productRepository.save(product);
    }

    //下架刪除商品
    public void deleteProduct(Long id) {
        productRepository.deleteById(id);
    }

    // 修改商品
    public Product updateProduct(Long id, Product updatedProduct) {
        return productRepository.findById(id)
                .map(existingProduct -> {
                    // 更新商品屬性
                    existingProduct.setName(updatedProduct.getName());
                    existingProduct.setDescription(updatedProduct.getDescription());
                    existingProduct.setPrice(updatedProduct.getPrice());
                    existingProduct.setStock(updatedProduct.getStock());
                    existingProduct.setCategory(updatedProduct.getCategory());
                    existingProduct.setImageUrl(updatedProduct.getImageUrl());
                    // 根據需要添加更多屬性更新邏輯
                    return productRepository.save(existingProduct);
                })
                .orElseThrow(() -> new RuntimeException("商品不存在，ID: " + id));
    }


}
