package Telecom.SubscriptionService.messaging;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;
import lombok.RequiredArgsConstructor;
import Telecom.SubscriptionService.model.Invoice;
import Telecom.SubscriptionService.repository.InvoiceRepository;
import java.time.Instant;

@Slf4j
@Service
@RequiredArgsConstructor
public class BillingEventListener {

    private final InvoiceRepository invoiceRepository;

    @RabbitListener(queues = BillingRabbitConfig.QUEUE, containerFactory = "billingListenerContainerFactory")
    public void handleBillingEvent(BillingEvent event) {
	validate(event);
	switch (event.getEventType()) {
	case SUBSCRIPTION_CREATED:
	    log.info("Processed billing event: subscription created for subscriptionId={}", event.getSubscriptionId());
	    break;
	case INVOICE_REQUESTED:
	    log.info("Processed billing event: invoice requested for subscriptionId={}", event.getSubscriptionId());
	    Invoice invoice = new Invoice();
	    invoice.setUserId(event.getUserId());
	    invoice.setSubscriptionId(event.getSubscriptionId());
	    invoice.setAmount(event.getAmount());
	    invoice.setStatus("PENDING");
	    invoice.setCreatedAt(Instant.now());
	    invoiceRepository.save(invoice);
	    break;
	case PAYMENT_FAILED:
	    log.warn("Processed billing event: payment failed for subscriptionId={}, message={}",
		    event.getSubscriptionId(), event.getMessage());
	    break;
	case SUPPORT_TICKET_RAISED:
	    log.warn("Processed billing event: support ticket raised for subscriptionId={}, message={}",
		    event.getSubscriptionId(), event.getMessage());
	    break;
	default:
	    throw new IllegalArgumentException("Unsupported billing event type: " + event.getEventType());
	}
    }

    private void validate(BillingEvent event) {
	if (event == null || event.getEventType() == null || event.getSubscriptionId() == null) {
	    throw new IllegalArgumentException("Invalid billing event payload");
	}
    }
}
