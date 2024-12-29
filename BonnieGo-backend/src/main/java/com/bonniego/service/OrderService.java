package com.bonniego.service;

import com.bonniego.model.CreateOrderRequest;
import com.bonniego.model.entity.ProductItem;
import com.bonniego.model.entity.Order;
import com.bonniego.model.entity.Product;
import com.bonniego.model.entity.User;
import com.bonniego.repository.OrderRepository;
import com.bonniego.repository.ProductItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class OrderService {

    @Autowired
    ProductService productService;
    @Autowired
    ProductItemRepository productItemRepository;
    @Autowired
    OrderRepository orderRepository;

    //建立訂單
    public void createOrder(User user, CreateOrderRequest request) {
    // Double totalPrice = request.getOrderItem().stream().mapToDouble(CreateOrderRequest.OrderItems::getSubtotal).sum();

        List<ProductItem> productItems = new ArrayList<>();
        request.getOrderItem().forEach(item->{
            ProductItem productItem = new ProductItem();
            productItem.setQuantity(item.getQuantity());
            productItem.setSubtotal(item.getSubtotal());
            Product product = productService.getProductById(item.getProductId());
            productItem.setName(product.getName());
            productItem.setImageUrl(product.getImageUrl());
            productItem.setUnitPrice(product.getPrice());
            productItems.add(productItem);
        });
        // 建立並保存訂單資料
        Order order = new Order();
        order.setUser(user);
        // order.setTotalPrice(totalPrice);
        order.setProductItems(productItems);
        order.setOrderDate(LocalDateTime.now());
        order.setRecipient(request.getRecipient());
        order.setAddress(request.getAddress());
        order.setPhone(request.getPhone());

        productItemRepository.saveAll(productItems);
        orderRepository.save(order);
    }

    // 單一用戶查找訂單(用戶歷史訂單)
    public List<Order> getOrdersByUser(User user) {
        return orderRepository.findByUserId(user.getId());
    }

    // 獲取所有訂單(管理者訂單管理)
    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    // 更新訂單狀態
    public boolean updateOrderStatus(Long orderId, String status) {
        Optional<Order> optionalOrder = orderRepository.findById(orderId);
        if (optionalOrder.isPresent()) {
            Order order = optionalOrder.get();
            order.setStatus(status);
            orderRepository.save(order);
            return true;
        }
        return false;
    }


}
