
package com.kalpana.destination;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class OrderKafkaConsumer {

    private static final Logger log =
            LoggerFactory.getLogger(OrderKafkaConsumer.class);

    private final ObjectMapper objectMapper;
    private final RestClient restClient;

    public OrderKafkaConsumer(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
        this.restClient = RestClient.builder()
                .baseUrl("http://localhost:8081")
                .build();
    }

    @KafkaListener(topics = "orders")
    public void consume(String message) throws Exception {

        JsonNode order = objectMapper.readTree(message);
        String orderId = order.path("orderId").asText("unknown");
        String correlationId =
                order.path("correlationId").asText("not-provided");

        log.info("Received order from Kafka: orderId={}, correlationId={}",
                orderId, correlationId);

        restClient.post()
                .uri("/processed-orders")
                .header("X-Correlation-ID", correlationId)
                .body(order)
                .retrieve()
                .toBodilessEntity();

        log.info("Forwarded order to Destination API: orderId={}", orderId);
    }
}

