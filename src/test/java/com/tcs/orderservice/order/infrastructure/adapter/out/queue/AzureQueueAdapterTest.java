package com.tcs.orderservice.order.infrastructure.adapter.out.queue;

import com.azure.storage.queue.QueueClient;
import com.azure.storage.queue.models.SendMessageResult;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.tcs.orderservice.order.domain.model.Order;
import com.tcs.orderservice.order.domain.model.OrderItem;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.Exceptions;
import reactor.test.StepVerifier;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AzureQueueAdapterTest {

    @Mock
    private QueueClient queueClient;

    private ObjectMapper objectMapper;

    private AzureQueueAdapter azureQueueAdapter;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        azureQueueAdapter = new AzureQueueAdapter(queueClient, objectMapper);
    }

    @Test
    void shouldSendBase64EncodedMessageToQueue() {
        Order order = buildOrder();
        when(queueClient.sendMessage(anyString())).thenReturn(mock(SendMessageResult.class));

        StepVerifier.create(azureQueueAdapter.send(order))
                .verifyComplete();

        ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
        verify(queueClient).sendMessage(captor.capture());

        String decoded = new String(Base64.getDecoder().decode(captor.getValue()));
        assertTrue(decoded.contains("ORD123"));
        assertTrue(decoded.contains("CUS001"));
        assertTrue(decoded.contains("PROD01"));
    }

    @Test
    void shouldRetryAndPropagateErrorWhenQueueClientFails() {
        Order order = buildOrder();
        when(queueClient.sendMessage(anyString()))
                .thenThrow(new RuntimeException("Connection refused"));

        StepVerifier.create(azureQueueAdapter.send(order))
                .expectErrorMatches(ex ->
                        Exceptions.isRetryExhausted(ex) &&
                        ex.getCause() instanceof RuntimeException &&
                        ex.getCause().getMessage().equals("Connection refused"))
                .verify(Duration.ofSeconds(30));

        verify(queueClient, atLeast(2)).sendMessage(anyString());
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
