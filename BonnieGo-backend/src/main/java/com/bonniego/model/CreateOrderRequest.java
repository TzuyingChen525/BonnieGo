package com.bonniego.model;

import lombok.Data;

import java.util.List;

@Data
public class CreateOrderRequest {
    private String recipient;
    private String address;
    private String phone;
    private List<OrderItems> orderItem;

    @Data
    public static class OrderItems{
        private Long productId;
        private int quantity;
        private Double subtotal;
    }
}
