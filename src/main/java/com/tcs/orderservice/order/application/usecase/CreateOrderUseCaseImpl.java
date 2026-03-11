package com.tcs.orderservice.order.application.usecase;

import com.tcs.orderservice.order.domain.model.Order;
import com.tcs.orderservice.order.domain.port.in.CreateOrderUseCase;
import com.tcs.orderservice.order.domain.port.out.OrderQueuePort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Slf4j
@Service
@RequiredArgsConstructor
public class CreateOrderUseCaseImpl implements CreateOrderUseCase {

    private final OrderQueuePort orderQueuePort;

    @Override
    public Mono<Order> execute(Order order) {
        return orderQueuePort.send(order)
                .thenReturn(order)
                .doOnSuccess(o -> log.info("Order sent to queue: {}", o.getOrderId()));
    }

}
