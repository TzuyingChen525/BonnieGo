package com.bonniego.controller;

import com.bonniego.exception.CartNotFoundException;
import com.bonniego.model.AddToCartRequest;
import com.bonniego.model.DeleteCartRequest;
import com.bonniego.model.UpdateCartRequest;
import com.bonniego.model.entity.CartItem;
import com.bonniego.repository.CartRepository;
import com.bonniego.service.CartService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cart")
public class CartController {

    @Autowired
    private CartService cartService;
    @Autowired
    private CartRepository cartRepository;

    // 獲取購物車內的所有項目
    @GetMapping
    public ResponseEntity<?> getCartItems(HttpSession session) {
        try {
            List<CartItem> cartItems = cartService.getCartItems(session);
            System.out.println("cartItems:"+cartItems);
            return ResponseEntity.ok(cartItems);
        } catch (CartNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("購物車不存在或未登入");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("無法獲取購物車項目"+"序列化錯誤");
        }
    }

    // 添加商品到購物車
    @PostMapping("/add")
    public ResponseEntity<String> addToCart(@Valid @RequestBody AddToCartRequest request, HttpSession session) {
        try {
            cartService.addToCart(session, request.getProduct_id(), request.getQuantity());
            return ResponseEntity.ok("商品已成功加入購物車");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("添加到購物車失敗: " + e.getMessage());
        }
    }

    // 更新購物車內的商品數量
    @PutMapping("/update")
    public ResponseEntity<String> updateCart(@RequestBody UpdateCartRequest request, HttpSession session) {
        try {
            cartService.updateCart(session, request.getProduct_id(), request.getQuantity());
            return ResponseEntity.ok("購物車更新成功");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("更新購物車失敗: " + e.getMessage());
        }
    }

    // 刪除購物車內的某個商品
    @DeleteMapping("/delete")
    public ResponseEntity<String> removeFromCart(@RequestBody DeleteCartRequest request, HttpSession session) {
        try {
            cartService.removeFromCart(session, request.getProduct_id());
            return ResponseEntity.ok("商品已成功從購物車移除");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("移除購物車商品失敗: " + e.getMessage());
        }
    }

    // 清空購物車資料
    @DeleteMapping("/clear")
    public ResponseEntity<String> clearCart(HttpSession session) {
        try {
            cartService.clearCart(session);
            return ResponseEntity.ok("購物車已清空");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("清空購物車失敗: " + e.getMessage());
        }
    }




}

