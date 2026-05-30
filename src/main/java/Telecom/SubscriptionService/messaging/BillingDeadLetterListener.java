package Telecom.SubscriptionService.messaging;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class BillingDeadLetterListener {

    @RabbitListener(queues = BillingRabbitConfig.DLQ)
    public void handleDeadLetter(BillingEvent event) {
	log.error("Dead-lettered billing event: type={}, subscriptionId={}, message={}", event.getEventType(),
		event.getSubscriptionId(), event.getMessage());
    }
}
