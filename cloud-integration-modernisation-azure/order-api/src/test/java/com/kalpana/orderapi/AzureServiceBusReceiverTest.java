
package com.kalpana.orderapi;

import com.azure.messaging.servicebus.ServiceBusClientBuilder;
import com.azure.messaging.servicebus.ServiceBusReceiverClient;
import com.azure.messaging.servicebus.models.ServiceBusReceiveMode;
import com.azure.messaging.servicebus.ServiceBusReceivedMessage;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.Duration;

@SpringBootTest
class AzureServiceBusReceiverTest {

    @Value("${azure.servicebus.connection-string}")
    private String connectionString;

    @Value("${azure.servicebus.queue-name}")
    private String queueName;

    @Test
    void receiveOrderMessage() {
        try (ServiceBusReceiverClient receiver =
                     new ServiceBusClientBuilder()
                             .connectionString(connectionString)
                             .receiver()
                             .queueName(queueName)
                             .receiveMode(ServiceBusReceiveMode.PEEK_LOCK)
                             .buildClient()) {

            Iterable<ServiceBusReceivedMessage> messages =
                    receiver.receiveMessages(1, Duration.ofSeconds(10));

            boolean received = false;

            for (ServiceBusReceivedMessage message : messages) {
                System.out.println("Azure Service Bus message received!");
                System.out.println("Message body: " + message.getBody());
                System.out.println("Message ID: " + message.getMessageId());

                receiver.complete(message);
                received = true;
            }

            if (!received) {
                System.out.println(
                        "No message received within 10 seconds. " +
                                "Check whether the Order API sent a message."
                );
            }
        }
    }
}
