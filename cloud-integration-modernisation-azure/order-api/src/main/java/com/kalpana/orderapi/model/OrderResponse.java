package com.kalpana.orderapi.model;
import java.math.BigDecimal;
public record OrderResponse(String orderId, String status, String message, BigDecimal amount, String currency, String correlationId) {}
