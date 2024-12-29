package com.bonniego.controller;


import com.bonniego.model.CreateOrderRequest;
import com.bonniego.model.entity.Order;
import com.bonniego.model.entity.User;
import com.bonniego.service.OrderService;
import com.bonniego.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/order")
public class OrderController {

    @Autowired
    private OrderService orderService;
    @Autowired
    private UserService userService;

    // 建立訂單
    @PostMapping
    public ResponseEntity<?> createOrder(HttpSession session, @RequestBody CreateOrderRequest request) {
        try {
            System.out.println("CreateOrderRequest:" + request);
            // 確認input
            if (request.getRecipient() == null || request.getRecipient().isEmpty() ||
                    request.getAddress() == null || request.getAddress().isEmpty() ||
                    request.getPhone() == null || request.getPhone().isEmpty() ||
                    request.getOrderItem() == null || request.getOrderItem().isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("缺少必要的收件或訂單資訊");
            }
            // 取得 user profile
            User user = userService.getUserProfile(session);
            if (user == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("用戶未登入或認證失敗");
            }
            // 建立訂單
            orderService.createOrder(user, request);
            return ResponseEntity.ok("訂單新增成功");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("無效的訂單資料：" + e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("伺服器內部錯誤：" + e.getMessage());
        }
    }

    // 取得單一用戶訂單資訊
    @GetMapping
    public ResponseEntity<?> getOrders(HttpSession session) {
        try {
            User user = userService.getUserProfile(session);
            if (user == null) {
                return ResponseEntity.status(401).body("用戶未登入");
            }

            List<Order> orders = orderService.getOrdersByUser(user);
            System.out.println(orders);
            return ResponseEntity.ok(orders);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body("無法獲取訂單資料：" + e.getMessage());
        }
    }


}

