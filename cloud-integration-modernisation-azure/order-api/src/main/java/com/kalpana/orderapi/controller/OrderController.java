

package com.kalpana.orderapi.controller;

import com.kalpana.orderapi.model.*;
import com.kalpana.orderapi.service.OrderKafkaProducer;
import com.kalpana.orderapi.service.AzureServiceBusProducer;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
import java.util.UUID;

@RestController
@RequestMapping("/orders")
public class OrderController {

 private final OrderKafkaProducer orderKafkaProducer;
 private final AzureServiceBusProducer azureServiceBusProducer;
 private final ObjectMapper objectMapper;

 public OrderController(
         OrderKafkaProducer orderKafkaProducer,
         AzureServiceBusProducer azureServiceBusProducer,
         ObjectMapper objectMapper) {

  this.orderKafkaProducer = orderKafkaProducer;
  this.azureServiceBusProducer = azureServiceBusProducer;
  this.objectMapper = objectMapper;
 }

 @PostMapping
 public ResponseEntity<OrderResponse> createOrder(
         @Valid @RequestBody OrderRequest request,
         @RequestHeader(value = "X-Correlation-ID", required = false)
         String suppliedId) throws JsonProcessingException {

  String correlationId =
          (suppliedId == null || suppliedId.isBlank())
                  ? UUID.randomUUID().toString()
                  : suppliedId;

  var message = new LinkedHashMap<String, Object>();
  message.put("orderId", request.orderId());
  message.put("amount", request.amount());
  message.put("currency", request.currency());
  message.put("correlationId", correlationId);

  String orderJson = objectMapper.writeValueAsString(message);

  // Existing Kafka integration
  orderKafkaProducer.publish(orderJson, request.orderId());

  // New Azure Service Bus integration
  azureServiceBusProducer.sendMessage(orderJson);

  return ResponseEntity.accepted().body(
          new OrderResponse(
                  request.orderId(),
                  "VALIDATED",
                  "Order request validated successfully",
                  request.amount(),
                  request.currency(),
                  correlationId
          )
  );
 }
}
