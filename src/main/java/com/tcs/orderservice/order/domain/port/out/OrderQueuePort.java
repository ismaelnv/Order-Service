package com.tcs.orderservice.order.domain.port.out;

import com.tcs.orderservice.order.domain.model.Order;
import reactor.core.publisher.Mono;

public interface OrderQueuePort {

    Mono<Void> send(Order order);

}
