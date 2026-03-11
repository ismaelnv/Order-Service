package com.tcs.orderservice.order.infrastructure.adapter.in.rest;

import com.tcs.orderservice.order.domain.model.Order;
import com.tcs.orderservice.order.domain.model.OrderItem;
import com.tcs.orderservice.order.domain.port.in.CreateOrderUseCase;
import com.tcs.orderservice.order.infrastructure.adapter.in.dto.OrderResponse;
import com.tcs.orderservice.order.infrastructure.adapter.in.mapper.OrderRestMapper;
import com.tcs.orderservice.shared.exception.GlobalExceptionHandler;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@WebFluxTest(OrderController.class)
@Import(GlobalExceptionHandler.class)
class OrderControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockitoBean
    private CreateOrderUseCase createOrderUseCase;

    @MockitoBean
    private OrderRestMapper orderRestMapper;

    @Test
    void shouldCreateOrderAndReturn201() {
        Order order = buildOrder();
        OrderResponse response = buildResponse();

        when(orderRestMapper.toDomain(any())).thenReturn(order);
        when(createOrderUseCase.execute(any())).thenReturn(Mono.just(order));
        when(orderRestMapper.toResponse(any())).thenReturn(response);

        String requestBody = """
                {
                    "orderId": "ORD123",
                    "customerId": "CUS001",
                    "items": [
                        { "productId": "PROD01", "quantity": 2 },
                        { "productId": "PROD02", "quantity": 1 }
                    ],
                    "totalAmount": 150.00
                }
                """;

        webTestClient.post()
                .uri("/api/v1/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(requestBody)
                .exchange()
                .expectStatus().isCreated()
                .expectBody()
                .jsonPath("$.status").isEqualTo(201)
                .jsonPath("$.message").isEqualTo("Order processed successfully")
                .jsonPath("$.order.orderId").isEqualTo("ORD123")
                .jsonPath("$.order.customerId").isEqualTo("CUS001")
                .jsonPath("$.order.totalAmount").isEqualTo(150.00)
                .jsonPath("$.order.items.length()").isEqualTo(2);
    }

    @Test
    void shouldReturn400WhenOrderIdIsBlank() {
        String requestBody = """
                {
                    "orderId": "",
                    "customerId": "CUS001",
                    "items": [
                        { "productId": "PROD01", "quantity": 2 }
                    ],
                    "totalAmount": 150.00
                }
                """;

        webTestClient.post()
                .uri("/api/v1/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(requestBody)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.status").isEqualTo(400)
                .jsonPath("$.messages").isNotEmpty();
    }

    @Test
    void shouldReturn400WhenItemsIsEmpty() {
        String requestBody = """
                {
                    "orderId": "ORD123",
                    "customerId": "CUS001",
                    "items": [],
                    "totalAmount": 150.00
                }
                """;

        webTestClient.post()
                .uri("/api/v1/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(requestBody)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.status").isEqualTo(400)
                .jsonPath("$.messages").isNotEmpty();
    }

    @Test
    void shouldReturn400WhenTotalAmountIsNegative() {
        String requestBody = """
                {
                    "orderId": "ORD123",
                    "customerId": "CUS001",
                    "items": [
                        { "productId": "PROD01", "quantity": 2 }
                    ],
                    "totalAmount": -10.00
                }
                """;

        webTestClient.post()
                .uri("/api/v1/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(requestBody)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.status").isEqualTo(400)
                .jsonPath("$.messages").isNotEmpty();
    }

    @Test
    void shouldReturn400WhenTotalAmountIsMissing() {
        String requestBody = """
                {
                    "orderId": "ORD123",
                    "customerId": "CUS001",
                    "items": [
                        { "productId": "PROD01", "quantity": 2 }
                    ]
                }
                """;

        webTestClient.post()
                .uri("/api/v1/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(requestBody)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.status").isEqualTo(400)
                .jsonPath("$.messages").isNotEmpty();
    }

    @Test
    void shouldReturn400WhenQuantityIsString() {
        String requestBody = """
                {
                    "orderId": "ORD123",
                    "customerId": "CUS001",
                    "items": [
                        { "productId": "PROD01", "quantity": "abc" }
                    ],
                    "totalAmount": 150.00
                }
                """;

        webTestClient.post()
                .uri("/api/v1/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(requestBody)
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    void shouldReturn400WhenCustomerIdIsMissing() {
        String requestBody = """
                {
                    "orderId": "ORD123",
                    "items": [
                        { "productId": "PROD01", "quantity": 2 }
                    ],
                    "totalAmount": 150.00
                }
                """;

        webTestClient.post()
                .uri("/api/v1/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(requestBody)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.status").isEqualTo(400)
                .jsonPath("$.messages").isNotEmpty();
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

    private OrderResponse buildResponse() {
        return OrderResponse.builder()
                .status(201)
                .message("Order processed successfully")
                .order(OrderResponse.OrderData.builder()
                        .orderId("ORD123")
                        .customerId("CUS001")
                        .items(List.of(
                                OrderResponse.OrderItemData.builder().productId("PROD01").quantity(2).build(),
                                OrderResponse.OrderItemData.builder().productId("PROD02").quantity(1).build()))
                        .totalAmount(new BigDecimal("150.00"))
                        .createdAt(LocalDateTime.now())
                        .build())
                .build();
    }

}
