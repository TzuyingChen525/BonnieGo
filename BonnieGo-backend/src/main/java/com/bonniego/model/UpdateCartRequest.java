package com.bonniego.model;

import lombok.Data;

@Data
public class UpdateCartRequest {
    private Long product_id;
    private Integer quantity;
}
