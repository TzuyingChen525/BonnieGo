package com.bonniego.controller;

import com.bonniego.model.entity.Order;
import com.bonniego.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/orders")
public class AdminOrderController {

    @Autowired
    private OrderService orderService;

    // 取得所有訂單
    @GetMapping
    public ResponseEntity<List<Order>> getAllOrders() {
        try {
            List<Order> orders = orderService.getAllOrders();
            return ResponseEntity.ok(orders);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body(null);
        }
    }

    // 更新訂單狀態
    @PutMapping("/{orderId}/status")
    public ResponseEntity<?> updateOrderStatus(@PathVariable Long orderId, @RequestBody Map<String, String> request) {
        try {
            String status = request.get("status");
            if (status == null || status.isEmpty()) {
                return ResponseEntity.badRequest().body("狀態不能為空");
            }

            boolean updated = orderService.updateOrderStatus(orderId, status);
            if (updated) {
                return ResponseEntity.ok("訂單狀態更新成功");
            } else {
                return ResponseEntity.status(404).body("訂單未找到");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body("無法更新訂單狀態：" + e.getMessage());
        }
    }
}

