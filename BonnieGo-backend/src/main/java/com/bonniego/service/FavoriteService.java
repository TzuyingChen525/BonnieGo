package com.bonniego.service;

import com.bonniego.exception.CartNotFoundException;
import com.bonniego.exception.ProductNotFoundException;
import com.bonniego.model.dto.UserCert;
import com.bonniego.model.entity.*;
import com.bonniego.repository.FavoriteItemRepository;
import com.bonniego.repository.FavoriteRepository;
import com.bonniego.repository.ProductRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class FavoriteService {

    @Autowired
    private FavoriteRepository favoriteRepository;
    @Autowired
    private FavoriteItemRepository favoriteItemRepository;
    @Autowired
    private ProductRepository productRepository;

    // 獲取用戶的收藏區
    private Favorite getUserFavorite(HttpSession session) {
        // 從 Session 中取得憑證
        UserCert userCert = (UserCert) session.getAttribute("userCert");
        //  System.out.println("查找單一會員，從 Session 獲取到的憑證: " + userCert);
        if (userCert == null) {
            System.err.println("錯誤: Session 中沒有找到 userCert！Session ID: " + session.getId() + " userCert:" + userCert);
            throw new CartNotFoundException("發生錯誤: 會員未登入");
        }
        return favoriteRepository.findByUserId(userCert.getId())
                .orElseGet(() -> createNewFavorite(userCert));
    }

    // 創建收藏區
    private Favorite createNewFavorite(UserCert userCert) {
        Favorite newFavorite = new Favorite();
        newFavorite.setUserId(userCert.getId());
        favoriteRepository.save(newFavorite);
        return newFavorite;
    }

    // 獲取列出收藏區商品
    public List<FavoriteItem> getFavoriteItems(HttpSession session) {
        Favorite favorite = getUserFavorite(session);
        //System.out.println(favorite.getId());
        List<FavoriteItem> items = favoriteItemRepository.findByFavoriteId(favorite.getId());
        //System.out.println(items);
        return items;
    }

    // 商品加入收藏區
    public void addToFavorite(HttpSession session, Long productId) {
        Favorite favorite = getUserFavorite(session);
        // 查找商品
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ProductNotFoundException("Product not found"));
        // 檢查收藏區中是否已有該商品
        Optional<FavoriteItem> existingFavoriteItem = favoriteItemRepository.findByFavoriteIdAndProduct(favorite.getId(), product);
        if (existingFavoriteItem.isPresent()) {
            throw new IllegalArgumentException("商品已經在收藏中");
        } else {
            FavoriteItem favoriteItem = new FavoriteItem();
            favoriteItem.setFavoriteId(favorite.getId());
            favoriteItem.setProduct(product);
            favoriteItemRepository.save(favoriteItem);
        }
    }

    //移除收藏區商品
    public void removeFromFavorite(HttpSession session, Long productId) {
        Favorite favorite = getUserFavorite(session);

        // 檢查商品是否存在於收藏區中
        Optional<FavoriteItem> favoriteItemOptional = favoriteItemRepository.findByFavoriteIdAndProduct(
                favorite.getId(),
                productRepository.findById(productId)
                        .orElseThrow(() -> new RuntimeException("商品不存在"))
        );

        if (favoriteItemOptional.isEmpty()) {
            throw new RuntimeException("收藏區中未找到該商品");
        }
        // 移除商品
        FavoriteItem favoriteItem = favoriteItemOptional.get();
        favoriteItemRepository.delete(favoriteItem);
    }


}

