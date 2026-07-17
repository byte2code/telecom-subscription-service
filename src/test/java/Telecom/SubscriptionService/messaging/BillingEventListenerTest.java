package Telecom.SubscriptionService.messaging;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import Telecom.SubscriptionService.repository.InvoiceRepository;

@ExtendWith(MockitoExtension.class)
class BillingEventListenerTest {

    @Mock
    private InvoiceRepository invoiceRepository;

    @InjectMocks
    private BillingEventListener billingEventListener;

    @Test
    void handleBillingEventRejectsInvalidPayload() {
	BillingEvent invalidEvent = BillingEvent.builder().message("missing type and subscription").build();

	assertThrows(IllegalArgumentException.class, () -> billingEventListener.handleBillingEvent(invalidEvent));
    }

    @Test
    void handleBillingEventAcceptsValidPayload() {
	BillingEvent validEvent = BillingEvent.builder()
		.eventType(BillingEventType.SUBSCRIPTION_CREATED)
		.subscriptionId(100L)
		.userId(11L)
		.planName("Silver Plan")
		.amount(499)
		.message("created")
		.source("subscription-service")
		.build();

	assertDoesNotThrow(() -> billingEventListener.handleBillingEvent(validEvent));
    }
}
