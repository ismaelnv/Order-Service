package com.tcs.orderservice.order.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
@AllArgsConstructor
public class Order {

    private final String orderId;
    private final String customerId;
    private final List<OrderItem> items;
    private final BigDecimal totalAmount;
    private final LocalDateTime createdAt;

}
