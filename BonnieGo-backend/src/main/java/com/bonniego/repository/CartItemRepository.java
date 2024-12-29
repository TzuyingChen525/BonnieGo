package com.bonniego.repository;

import com.bonniego.model.entity.CartItem;
import com.bonniego.model.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem, Long> {
    // 查找購物車的所有購物車項目
     List<CartItem> findByCartId(Long cartId);

    // 查找購物車中特定商品的項目
    Optional<CartItem> findByCartIdAndProduct(Long cartId, Product product);
}

