package com.tcs.orderservice.order.infrastructure.adapter.in.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
@AllArgsConstructor
@Schema(description = "Response for order creation")
public class OrderResponse {

    @Schema(description = "HTTP status code", example = "201")
    private final int status;

    @Schema(description = "Descriptive result message", example = "Order processed successfully")
    private final String message;

    @Schema(description = "Created order data")
    private final OrderData order;

    @Getter
    @Builder
    @AllArgsConstructor
    @Schema(description = "Order data")
    public static class OrderData {

        @Schema(description = "Unique order identifier", example = "ORD123")
        private final String orderId;

        @Schema(description = "Customer identifier", example = "CUS001")
        private final String customerId;

        @Schema(description = "List of order items")
        private final List<OrderItemData> items;

        @Schema(description = "Total order amount", example = "150.00")
        private final BigDecimal totalAmount;

        @Schema(description = "Order creation timestamp")
        private final LocalDateTime createdAt;
    }

    @Getter
    @Builder
    @AllArgsConstructor
    @Schema(description = "Order item detail")
    public static class OrderItemData {

        @Schema(description = "Product identifier", example = "PROD01")
        private final String productId;

        @Schema(description = "Product quantity", example = "2")
        private final Integer quantity;
    }

}
