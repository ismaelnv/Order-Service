package com.tcs.orderservice.order.application.usecase;

import com.tcs.orderservice.order.domain.model.Order;
import com.tcs.orderservice.order.domain.model.OrderItem;
import com.tcs.orderservice.order.domain.port.out.OrderQueuePort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CreateOrderUseCaseImplTest {

    @Mock
    private OrderQueuePort orderQueuePort;

    @InjectMocks
    private CreateOrderUseCaseImpl createOrderUseCase;

    @Test
    void shouldSendOrderToQueueAndReturnIt() {
        Order order = buildOrder();
        when(orderQueuePort.send(any(Order.class))).thenReturn(Mono.empty());

        StepVerifier.create(createOrderUseCase.execute(order))
                .expectNextMatches(result ->
                        result.getOrderId().equals("ORD123") &&
                        result.getCustomerId().equals("CUS001") &&
                        result.getTotalAmount().compareTo(new BigDecimal("150.00")) == 0 &&
                        result.getItems().size() == 2)
                .verifyComplete();

        verify(orderQueuePort).send(order);
    }

    @Test
    void shouldPropagateErrorWhenQueueFails() {
        Order order = buildOrder();
        when(orderQueuePort.send(any(Order.class)))
                .thenReturn(Mono.error(new RuntimeException("Queue connection failed")));

        StepVerifier.create(createOrderUseCase.execute(order))
                .expectErrorMatches(ex ->
                        ex instanceof RuntimeException &&
                        ex.getMessage().equals("Queue connection failed"))
                .verify();
    }

    private Order buildOrder() {
        return Order.builder()
                .orderId("ORD123")
                .customerId("CUS001")
                .items(List.of(
                        OrderItem.builder().productId("PROD01").quantity(2).build(),
                        OrderItem.builder().productId("PROD02").quantity(1).build()))
                .totalAmount(new BigDecimal("150.00"))
                .createdAt(LocalDateTime.now())
                .build();
    }

}
