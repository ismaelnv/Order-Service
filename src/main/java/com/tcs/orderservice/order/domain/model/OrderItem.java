package com.tcs.orderservice.order.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class OrderItem {

    private final String productId;
    private final Integer quantity;

}
