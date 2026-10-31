package com.kalpana.destination;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;
@RestController public class ProcessedOrderController {
 private static final Logger log = LoggerFactory.getLogger(ProcessedOrderController.class);
 @PostMapping("/processed-orders")
 public ResponseEntity<Map<String,String>> process(@RequestBody Map<String,Object> order,
   @RequestHeader(value="X-Correlation-ID", required=false) String correlationId) {
  String orderId = String.valueOf(order.getOrDefault("orderId","unknown"));
  log.info("Received orderId={} correlationId={}", orderId, correlationId);
  return ResponseEntity.accepted().body(Map.of("orderId",orderId,"status","RECEIVED",
   "correlationId", correlationId == null ? "not-provided" : correlationId));
 }
}
