package Telecom.SubscriptionService.messaging;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BillingRabbitConfig {

    public static final String EXCHANGE = "telecom.billing.exchange";
    public static final String QUEUE = "telecom.billing.queue";

    public static final String ROUTING_SUBSCRIPTION_CREATED = "billing.subscription.created";
    public static final String ROUTING_INVOICE_REQUESTED = "billing.invoice.requested";
    public static final String ROUTING_PAYMENT_FAILED = "billing.payment.failed";
    public static final String ROUTING_SUPPORT_TICKET_RAISED = "billing.support.ticket.raised";

    @Bean
    public TopicExchange billingExchange() {
	return new TopicExchange(EXCHANGE);
    }

    @Bean
    public Queue billingQueue() {
	return new Queue(QUEUE, true);
    }

    @Bean
    public Binding billingBinding(Queue billingQueue, TopicExchange billingExchange) {
	return BindingBuilder.bind(billingQueue).to(billingExchange).with("billing.#");
    }

    @Bean
    public Jackson2JsonMessageConverter jackson2JsonMessageConverter() {
	return new Jackson2JsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory,
	    Jackson2JsonMessageConverter jackson2JsonMessageConverter) {
	RabbitTemplate template = new RabbitTemplate(connectionFactory);
	template.setMessageConverter(jackson2JsonMessageConverter);
	return template;
    }
}
