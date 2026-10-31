package com.kalpana.orderapi;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
@SpringBootTest @AutoConfigureMockMvc
class OrderControllerTest {
 @Autowired MockMvc mockMvc;
 @Test void acceptsValidOrder() throws Exception {
  mockMvc.perform(post("/orders").contentType(MediaType.APPLICATION_JSON)
   .header("X-Correlation-ID","test-correlation-123")
   .content("{\"orderId\":\"ORD-1001\",\"customerId\":\"CUST-101\",\"amount\":249.99,\"currency\":\"EUR\"}"))
   .andExpect(status().isAccepted()).andExpect(jsonPath("$.orderId").value("ORD-1001"))
   .andExpect(jsonPath("$.status").value("VALIDATED")).andExpect(jsonPath("$.correlationId").value("test-correlation-123"));
 }
 @Test void rejectsInvalidAmount() throws Exception {
  mockMvc.perform(post("/orders").contentType(MediaType.APPLICATION_JSON)
   .content("{\"orderId\":\"ORD-1001\",\"customerId\":\"CUST-101\",\"amount\":0,\"currency\":\"EUR\"}"))
   .andExpect(status().isBadRequest());
 }
}
