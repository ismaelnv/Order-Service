package com.tcs.orderservice.order.infrastructure.adapter.out.queue;

import com.azure.storage.queue.QueueClient;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tcs.orderservice.order.domain.model.Order;
import com.tcs.orderservice.order.domain.port.out.OrderQueuePort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.Base64;

@Slf4j
@Component
@RequiredArgsConstructor
public class AzureQueueAdapter implements OrderQueuePort {

    private final QueueClient queueClient;
    private final ObjectMapper objectMapper;

    @Override
    public Mono<Void> send(Order order) {
        return Mono.fromCallable(() -> {
                    String json = objectMapper.writeValueAsString(order);
                    String encodedMessage = Base64.getEncoder().encodeToString(json.getBytes());
                    queueClient.sendMessage(encodedMessage);
                    log.info("Message sent to Azure Queue for order: {}", order.getOrderId());
                    return true;
                })
                .subscribeOn(Schedulers.boundedElastic())
                .then();
    }

}
