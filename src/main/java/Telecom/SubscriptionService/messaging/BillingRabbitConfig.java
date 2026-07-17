package Telecom.SubscriptionService.messaging;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.config.RetryInterceptorBuilder;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.rabbit.retry.RepublishMessageRecoverer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BillingRabbitConfig {

    public static final String EXCHANGE = "telecom.billing.exchange";
    public static final String DLX = "telecom.billing.dlx";
    public static final String QUEUE = "telecom.billing.queue";
    public static final String DLQ = "telecom.billing.dlq";

    public static final String ROUTING_SUBSCRIPTION_CREATED = "billing.subscription.created";
    public static final String ROUTING_INVOICE_REQUESTED = "billing.invoice.requested";
    public static final String ROUTING_PAYMENT_FAILED = "billing.payment.failed";
    public static final String ROUTING_SUPPORT_TICKET_RAISED = "billing.support.ticket.raised";
    public static final String ROUTING_RENEWAL_REQUESTED = "billing.renewal.requested";
    public static final String ROUTING_DLQ = "billing.dead";

    @Bean
    public TopicExchange billingExchange() {
	return new TopicExchange(EXCHANGE);
    }

    @Bean
    public Queue billingQueue() {
	return new Queue(QUEUE, true);
    }

    @Bean
    public DirectExchange billingDeadLetterExchange() {
	return new DirectExchange(DLX);
    }

    @Bean
    public Queue billingDeadLetterQueue() {
	return new Queue(DLQ, true);
    }

    @Bean
    public Binding billingBinding(Queue billingQueue, TopicExchange billingExchange) {
	return BindingBuilder.bind(billingQueue).to(billingExchange).with("billing.#");
    }

    @Bean
    public Binding billingDeadLetterBinding(Queue billingDeadLetterQueue, DirectExchange billingDeadLetterExchange) {
	return BindingBuilder.bind(billingDeadLetterQueue).to(billingDeadLetterExchange).with(ROUTING_DLQ);
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

    @Bean
    public SimpleRabbitListenerContainerFactory billingListenerContainerFactory(ConnectionFactory connectionFactory,
	    Jackson2JsonMessageConverter jackson2JsonMessageConverter, RabbitTemplate rabbitTemplate) {
	SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
	factory.setConnectionFactory(connectionFactory);
	factory.setMessageConverter(jackson2JsonMessageConverter);
	factory.setDefaultRequeueRejected(false);
	factory.setAdviceChain(RetryInterceptorBuilder.stateless()
		.maxAttempts(3)
		.backOffOptions(1000, 2.0, 5000)
		.recoverer(new RepublishMessageRecoverer(rabbitTemplate, DLX, ROUTING_DLQ))
		.build());
	return factory;
    }
}
