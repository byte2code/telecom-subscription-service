package Telecom.SubscriptionService.messaging;

import java.time.Instant;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import Telecom.SubscriptionService.model.Subscription;

@Service
@RequiredArgsConstructor
public class BillingEventPublisher {

    private final RabbitTemplate rabbitTemplate;

    public void publishSubscriptionCreated(Subscription subscription) {
	publish(BillingEventType.SUBSCRIPTION_CREATED, BillingRabbitConfig.ROUTING_SUBSCRIPTION_CREATED, subscription,
		"Subscription created in REQUESTED state");
    }

    public void publishInvoiceRequested(Subscription subscription) {
	publish(BillingEventType.INVOICE_REQUESTED, BillingRabbitConfig.ROUTING_INVOICE_REQUESTED, subscription,
		"Invoice requested from billing service");
    }

    public void publishRenewalRequested(Subscription subscription) {
	publish(BillingEventType.RENEWAL_REQUESTED, BillingRabbitConfig.ROUTING_RENEWAL_REQUESTED, subscription,
		"Renewal requested for subscription");
    }

    public void publishPaymentFailed(Subscription subscription, String reason) {
	publish(BillingEventType.PAYMENT_FAILED, BillingRabbitConfig.ROUTING_PAYMENT_FAILED, subscription, reason);
    }

    public void publishSupportTicketRaised(Subscription subscription, String reason) {
	publish(BillingEventType.SUPPORT_TICKET_RAISED, BillingRabbitConfig.ROUTING_SUPPORT_TICKET_RAISED, subscription,
		reason);
    }

    private void publish(BillingEventType eventType, String routingKey, Subscription subscription, String message) {
	BillingEvent event = BillingEvent.builder()
		.eventType(eventType)
		.subscriptionId(subscription.getId())
		.userId(subscription.getUser() != null ? subscription.getUser().getId() : null)
		.planName(subscription.getPlan() != null ? subscription.getPlan().getName() : null)
		.amount(subscription.getPlan() != null ? subscription.getPlan().getPrice() : null)
		.message(message)
		.source("subscription-service")
		.createdAt(Instant.now())
		.build();
	rabbitTemplate.convertAndSend(BillingRabbitConfig.EXCHANGE, routingKey, event);
    }
}
