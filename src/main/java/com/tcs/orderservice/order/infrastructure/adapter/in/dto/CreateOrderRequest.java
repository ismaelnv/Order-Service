package com.tcs.orderservice.order.infrastructure.adapter.in.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Input data for creating an order")
public class CreateOrderRequest {

    @NotBlank(message = "orderId is required")
    @Schema(description = "Unique order identifier", example = "ORD123")
    private String orderId;

    @NotBlank(message = "customerId is required")
    @Schema(description = "Customer identifier", example = "CUS001")
    private String customerId;

    @NotEmpty(message = "Order must have at least one item")
    @Valid
    @Schema(description = "List of order items")
    private List<OrderItemRequest> items;

    @NotNull(message = "totalAmount is required")
    @Positive(message = "totalAmount must be greater than 0")
    @Schema(description = "Total order amount", example = "150.00")
    private BigDecimal totalAmount;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "Order item detail")
    public static class OrderItemRequest {

        @NotBlank(message = "productId is required")
        @Schema(description = "Product identifier", example = "PROD01")
        private String productId;

        @NotNull(message = "quantity is required")
        @Positive(message = "quantity must be greater than 0")
        @Schema(description = "Product quantity", example = "2")
        private Integer quantity;
    }

}
