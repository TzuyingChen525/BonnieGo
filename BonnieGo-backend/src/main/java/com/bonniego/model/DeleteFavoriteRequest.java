package com.bonniego.model;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class DeleteFavoriteRequest {
    @NotNull(message = "商品ID不能為空")
    private Long productId;
}
