package com.kalpana.function;
import com.microsoft.azure.functions.*;
import com.microsoft.azure.functions.annotation.*;
import java.net.URI;
import java.net.http.*;
import java.time.Duration;
import java.util.Optional;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class OrderProcessorFunction {
 private static final ObjectMapper MAPPER = new ObjectMapper();
 private static final HttpClient HTTP = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(5)).build();

 @FunctionName("OrderProcessor")
 public void run(@ServiceBusQueueTrigger(name="message", queueName="%ORDER_QUEUE_NAME%", connection="ServiceBusConnection")
                 String message, final ExecutionContext context) throws Exception {
  String destinationUrl = System.getenv("DESTINATION_API_URL");
  if (destinationUrl == null || destinationUrl.isBlank()) throw new IllegalStateException("DESTINATION_API_URL is missing");
  JsonNode order = MAPPER.readTree(message);
  String orderId = Optional.ofNullable(order.get("orderId")).map(JsonNode::asText).orElse("unknown");
  String correlationId = Optional.ofNullable(order.get("correlationId")).map(JsonNode::asText).orElse(orderId);
  context.getLogger().info("Processing orderId=" + orderId + " correlationId=" + correlationId);
  HttpRequest request = HttpRequest.newBuilder(URI.create(destinationUrl)).timeout(Duration.ofSeconds(20))
   .header("Content-Type","application/json").header("X-Correlation-ID",correlationId)
   .POST(HttpRequest.BodyPublishers.ofString(message)).build();
  HttpResponse<String> response = HTTP.send(request,HttpResponse.BodyHandlers.ofString());
  if (response.statusCode() < 200 || response.statusCode() >= 300)
   throw new IllegalStateException("Destination API returned HTTP " + response.statusCode());
  context.getLogger().info("Delivered orderId=" + orderId + " destinationStatus=" + response.statusCode());
 }
}
