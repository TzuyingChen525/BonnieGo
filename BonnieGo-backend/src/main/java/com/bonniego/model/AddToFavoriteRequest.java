package com.bonniego.model;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AddToFavoriteRequest {
    @NotNull(message = "商品ID不能為空")
    private Long productId;
}
