package com.bonniego.controller;

import com.bonniego.exception.FavoriteNotFoundException;
import com.bonniego.model.AddToFavoriteRequest;
import com.bonniego.model.DeleteFavoriteRequest;
import com.bonniego.model.entity.FavoriteItem;
import com.bonniego.service.FavoriteService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/favorites")
public class FavoriteController {

    @Autowired
    private FavoriteService favoriteService;

    // 獲取收藏清單商品
    @GetMapping
    public ResponseEntity<?> getFavoriteItems(HttpSession session) {
        try {
            List<FavoriteItem> favoriteItems = favoriteService.getFavoriteItems(session);
            System.out.println("favoriteItems:"+favoriteItems);
            return ResponseEntity.ok(favoriteItems);
        } catch (FavoriteNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("收藏區不存在或未登入");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("無法獲取收藏區項目");
        }
    }

    // 加入收藏商品
    @PostMapping("/add")
    public ResponseEntity<String> addToFavorite(@Valid @RequestBody AddToFavoriteRequest request, HttpSession session) {
        try {
            favoriteService.addToFavorite(session,request.getProductId());
            return ResponseEntity.ok("商品已成功加入收藏區");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("添加到收藏區失敗: " + e.getMessage());
        }
    }

    // 移除收藏商品
    @DeleteMapping("/delete")
    public ResponseEntity<String> removeFromFavorite(@RequestBody DeleteFavoriteRequest request, HttpSession session) {
        if (request.getProductId() == null) {
            return ResponseEntity.badRequest().body("商品 ID 不能為空！");
        }
        try {
            favoriteService.removeFromFavorite(session, request.getProductId());
            return ResponseEntity.ok("商品已成功從收藏區移除");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("移除收藏區商品失敗: " + e.getMessage());
        }
    }

}

