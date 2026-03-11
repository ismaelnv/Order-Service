package com.tcs.orderservice.order.domain.port.in;

import com.tcs.orderservice.order.domain.model.Order;
import reactor.core.publisher.Mono;

public interface CreateOrderUseCase {

    Mono<Order> execute(Order order);

}
