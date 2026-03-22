package com.tcs.orderservice.order.infrastructure.adapter.in.rest;

import com.tcs.orderservice.order.domain.port.in.CreateOrderUseCase;
import com.tcs.orderservice.order.infrastructure.adapter.in.dto.CreateOrderRequest;
import com.tcs.orderservice.order.infrastructure.adapter.in.dto.OrderResponse;
import com.tcs.orderservice.order.infrastructure.adapter.in.mapper.OrderRestMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
@Tag(name = "Orders", description = "Endpoint for order management")
public class OrderController {

    private final CreateOrderUseCase createOrderUseCase;
    private final OrderRestMapper orderRestMapper;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(
            summary = "Create an order",
            description = "Receives an order, validates it and sends it to the Azure Queue Storage for asynchronous processing."
    )
    @ApiResponse(responseCode = "201", description = "Order created and sent to queue successfully",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = OrderResponse.class)))
    @ApiResponse(responseCode = "400", description = "Validation error in order data",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                    examples = @ExampleObject(value = """
                            {
                                "timestamp": "2026-03-11T15:07:55",
                                "status": 400,
                                "error": "Bad Request",
                                "messages": ["orderId: orderId is required"]
                            }
                            """)))
    public Mono<ResponseEntity<OrderResponse>> createOrder(@Valid @RequestBody CreateOrderRequest request) {
        return Mono.just(request)
                .map(orderRestMapper::toDomain)
                .flatMap(createOrderUseCase::execute)
                .map(orderRestMapper::toResponse)
                .map(orderResponse -> ResponseEntity
                        .status(HttpStatus.CREATED)
                        .body(orderResponse)
                );
    }

}
