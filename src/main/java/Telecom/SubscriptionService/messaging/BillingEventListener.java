package Telecom.SubscriptionService.messaging;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class BillingEventListener {

    @RabbitListener(queues = BillingRabbitConfig.QUEUE)
    public void handleBillingEvent(BillingEvent event) {
	log.info("Received billing event: type={}, subscriptionId={}, message={}", event.getEventType(),
		event.getSubscriptionId(), event.getMessage());
    }
}
