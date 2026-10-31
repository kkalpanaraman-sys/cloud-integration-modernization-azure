package com.kalpana.orderapi.model;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
public record OrderRequest(
 @NotBlank(message="orderId is required") String orderId,
 @NotBlank(message="customerId is required") String customerId,
 @NotNull(message="amount is required") @DecimalMin(value="0.01", message="amount must be greater than zero") BigDecimal amount,
 @NotBlank(message="currency is required") @Pattern(regexp="^[A-Z]{3}$", message="currency must be a 3-letter uppercase code") String currency
) {}
