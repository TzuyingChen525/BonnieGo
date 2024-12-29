package com.bonniego.model;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AddToCartRequest {
    @NotNull(message = "商品ID不能為空")
    private Long product_id;

    @Min(value = 1, message = "數量至少為1")
    private Integer quantity;

}
