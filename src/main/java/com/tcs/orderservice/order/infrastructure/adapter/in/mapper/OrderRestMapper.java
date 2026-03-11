package com.tcs.orderservice.order.infrastructure.adapter.in.mapper;

import com.tcs.orderservice.order.domain.model.Order;
import com.tcs.orderservice.order.domain.model.OrderItem;
import com.tcs.orderservice.order.infrastructure.adapter.in.dto.CreateOrderRequest;
import com.tcs.orderservice.order.infrastructure.adapter.in.dto.OrderResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface OrderRestMapper {

    @Mapping(target = "createdAt", expression = "java(java.time.LocalDateTime.now())")
    Order toDomain(CreateOrderRequest request);

    OrderResponse.OrderData toOrderData(Order order);

    OrderResponse.OrderItemData toOrderItemData(OrderItem item);

    default OrderResponse toResponse(Order order) {
        return OrderResponse.builder()
                .status(201)
                .message("Order processed successfully")
                .order(toOrderData(order))
                .build();
    }

}
