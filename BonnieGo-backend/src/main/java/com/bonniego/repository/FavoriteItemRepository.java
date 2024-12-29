package com.bonniego.repository;

import com.bonniego.model.entity.FavoriteItem;
import com.bonniego.model.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FavoriteItemRepository extends JpaRepository<FavoriteItem, Long> {
    // 查找收藏商品的所有收藏項目
     List<FavoriteItem> findByFavoriteId(Long favoriteId);

    // 查找購物車中特定商品的項目
     Optional<FavoriteItem> findByFavoriteIdAndProduct(Long favoriteId, Product product);
}

