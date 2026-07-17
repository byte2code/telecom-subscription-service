package Telecom.SubscriptionService.messaging;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

import org.junit.jupiter.api.Test;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.Queue;

class BillingRabbitConfigTest {

    private final BillingRabbitConfig billingRabbitConfig = new BillingRabbitConfig();

    @Test
    void createsPrimaryAndDeadLetterResources() {
	Queue queue = billingRabbitConfig.billingQueue();
	Queue deadLetterQueue = billingRabbitConfig.billingDeadLetterQueue();
	Binding dlqBinding = billingRabbitConfig.billingDeadLetterBinding(deadLetterQueue,
		billingRabbitConfig.billingDeadLetterExchange());

	assertEquals(BillingRabbitConfig.QUEUE, queue.getName());
	assertEquals(BillingRabbitConfig.DLQ, deadLetterQueue.getName());
	assertNotNull(dlqBinding);
    }

    @Test
    void createsRetryAwareListenerFactory() {
	ConnectionFactory connectionFactory = mock(ConnectionFactory.class);
	RabbitTemplate rabbitTemplate = mock(RabbitTemplate.class);
	com.fasterxml.jackson.databind.ObjectMapper objectMapper = mock(com.fasterxml.jackson.databind.ObjectMapper.class);

	assertNotNull(billingRabbitConfig.billingListenerContainerFactory(connectionFactory,
		billingRabbitConfig.jackson2JsonMessageConverter(objectMapper), rabbitTemplate));
    }
}
