package Telecom.SubscriptionService.messaging;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import Telecom.SubscriptionService.model.Subscription;
import Telecom.SubscriptionService.model.User;

@ExtendWith(MockitoExtension.class)
class BillingEventPublisherTest {

    @Mock
    private RabbitTemplate rabbitTemplate;

    private BillingEventPublisher billingEventPublisher;

    @BeforeEach
    void setUp() {
	billingEventPublisher = new BillingEventPublisher(rabbitTemplate);
    }

    @Test
    void publishSubscriptionCreatedSendsBillingEventToExchange() {
	Subscription subscription = subscription(41L, 7L, "Silver Plan", 499);

	billingEventPublisher.publishSubscriptionCreated(subscription);

	ArgumentCaptor<BillingEvent> eventCaptor = ArgumentCaptor.forClass(BillingEvent.class);
	verify(rabbitTemplate).convertAndSend(eq(BillingRabbitConfig.EXCHANGE),
		eq(BillingRabbitConfig.ROUTING_SUBSCRIPTION_CREATED), eventCaptor.capture());
	assertEquals(BillingEventType.SUBSCRIPTION_CREATED, eventCaptor.getValue().getEventType());
	assertEquals(41L, eventCaptor.getValue().getSubscriptionId());
    }

    @Test
    void publishPaymentFailedUsesDedicatedRoutingKey() {
	Subscription subscription = subscription(42L, 7L, "Gold Plan", 699);

	billingEventPublisher.publishPaymentFailed(subscription, "Payment gateway timeout");

	ArgumentCaptor<BillingEvent> eventCaptor = ArgumentCaptor.forClass(BillingEvent.class);
	verify(rabbitTemplate).convertAndSend(eq(BillingRabbitConfig.EXCHANGE),
		eq(BillingRabbitConfig.ROUTING_PAYMENT_FAILED), eventCaptor.capture());
	assertTrue(eventCaptor.getValue().getMessage().contains("timeout"));
    }

    private Subscription subscription(Long subscriptionId, Long userId, String planName, Integer price) {
	User user = new User();
	user.setId(userId);
	Telecom.SubscriptionService.model.Plan plan = new Telecom.SubscriptionService.model.Plan();
	plan.setName(planName);
	plan.setPrice(price);
	Subscription subscription = new Subscription();
	subscription.setId(subscriptionId);
	subscription.setUser(user);
	subscription.setPlan(plan);
	return subscription;
    }
}
