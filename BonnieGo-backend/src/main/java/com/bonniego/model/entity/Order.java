package com.bonniego.model.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Data
@Table(name = "orders")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    private Long id;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")  // 設置日期時間格式
    @Column
    private LocalDateTime orderDate;

    @ElementCollection
    private List<ProductItem> productItems; //訂單內被購買項目

    private String status = "待處理";
    private String recipient;
    private String address;
    private String phone;

    // order與user的關係是多對1關聯
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

}


