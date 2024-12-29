package com.bonniego.controller;

import com.bonniego.model.entity.Product;
import com.bonniego.service.FileStorageService;
import com.bonniego.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/products")
public class ProductController {
    @Autowired
    private ProductService productService;
    @Autowired
    private FileStorageService fileStorageService;

    //列出所有商品（支援篩選與分頁）
    @GetMapping
    public ResponseEntity<Map<String, Object>> getProducts(
            @RequestParam(defaultValue = "all") String category,
            @RequestParam(defaultValue = "all") String brand,
            @RequestParam(defaultValue = "price-asc") String sortOption,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "15") int size) {

        Page<Product> products = productService.getFilteredProducts(category, brand, sortOption, page, size);
        Map<String, Object> response = Map.of(
                "content", products.getContent(),
                "totalPages", products.getTotalPages(),
                "currentPage", products.getNumber() + 1 // 分頁從 0 開始
        );

        return ResponseEntity.ok(response);
    }


    //用keyword查找商品
//    @GetMapping("/search")
//    public List<Product> searchProducts(@RequestParam String keyword) {
//        return productService.searchProductsByName(keyword);
//    }

    //上傳圖片
    @PostMapping("/upload")
    public ResponseEntity<Map<String, String>> uploadImage(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "File is empty"));
        }
        try {
            // 保存檔案 (假設保存到本地 uploads 資料夾)
            String uploadDir = "uploads/";
            String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
            Path filePath = Paths.get(uploadDir + fileName);
            Files.createDirectories(filePath.getParent());
            Files.write(filePath, file.getBytes());

            // 返回圖片的公開 URL (假設是靜態路徑)
            String fileUrl = "http://localhost:8080/uploads/" + fileName;
            return ResponseEntity.ok(Map.of("url", fileUrl));

        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "File upload failed"));
        }
    }

    //新增商品
    @PostMapping
    public ResponseEntity<Product> addProduct(@RequestBody Product product) {
        System.out.println(product);
        Product savedProduct = productService.addProduct(product);
        return ResponseEntity.status(201).body(savedProduct);
    }

    //下架刪除商品
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }

    //修改商品
    @PutMapping("/{id}")
    public ResponseEntity<Product> updateProduct(@PathVariable Long id, @RequestBody Product updatedProduct) {
        Product product = productService.updateProduct(id, updatedProduct);
        return product != null ? ResponseEntity.ok(product) : ResponseEntity.notFound().build();
    }


}

