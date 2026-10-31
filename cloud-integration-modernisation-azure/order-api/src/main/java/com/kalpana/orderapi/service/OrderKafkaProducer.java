
package com.kalpana.orderapi.service;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class OrderKafkaProducer {
    private static final String TOPIC = "orders";

    private final KafkaTemplate<String, String> kafkaTemplate;

    public OrderKafkaProducer(KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void publish(String orderJson, String orderId) {
        kafkaTemplate.send(TOPIC, orderId, orderJson);
    }

}
