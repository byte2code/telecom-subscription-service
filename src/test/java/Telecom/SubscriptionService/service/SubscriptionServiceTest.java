package Telecom.SubscriptionService.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import Telecom.SubscriptionService.dto.SubscriptionDto;
import Telecom.SubscriptionService.feign.BillingService;
import Telecom.SubscriptionService.feign.SupportService;
import Telecom.SubscriptionService.messaging.BillingEventPublisher;
import Telecom.SubscriptionService.model.Subscription;
import Telecom.SubscriptionService.model.SubscriptionStatus;
import Telecom.SubscriptionService.model.User;
import Telecom.SubscriptionService.repository.SubscriptionRepository;
import Telecom.SubscriptionService.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
class SubscriptionServiceTest {

    @Mock
    private SubscriptionRepository subscriptionRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private BillingService billingService;

    @Mock
    private SupportService supportService;

    @Mock
    private BillingEventPublisher billingEventPublisher;

    @InjectMocks
    private SubscriptionService subscriptionService;

    private User user;

    @BeforeEach
    void setUp() {
	user = new User();
	user.setId(1L);
	user.setName("Asha Patel");
    }

    @Test
    void createSubscriptionMovesFromRequestedToActiveWhenBillingSucceeds() {
	SubscriptionDto dto = new SubscriptionDto(499, "Silver Plan", "Monthly pack", 1L);

	when(userRepository.findById(1L)).thenReturn(Optional.of(user));
	when(subscriptionRepository.save(any(Subscription.class))).thenAnswer(invocation -> invocation.getArgument(0));
	when(billingService.createInvoice(any())).thenReturn("ok");

	Subscription subscription = subscriptionService.createSubscription(dto);

	assertNotNull(subscription);
	assertEquals(SubscriptionStatus.ACTIVE, subscription.getStatus());
	verify(billingService, times(1)).createInvoice(any());
	verify(billingEventPublisher, times(1)).publishSubscriptionCreated(any());
	verify(billingEventPublisher, times(1)).publishInvoiceRequested(any());
    }

    @Test
    void createSubscriptionMarksPaymentFailedWhenBillingFails() {
	SubscriptionDto dto = new SubscriptionDto(699, "Gold Plan", "Annual pack", 1L);

	when(userRepository.findById(1L)).thenReturn(Optional.of(user));
	when(subscriptionRepository.save(any(Subscription.class))).thenAnswer(invocation -> invocation.getArgument(0));
	doThrow(new RuntimeException("billing unavailable")).when(billingService).createInvoice(any());

	Subscription subscription = subscriptionService.createSubscription(dto);

	assertNotNull(subscription);
	assertEquals(SubscriptionStatus.PAYMENT_FAILED, subscription.getStatus());
	verify(billingService, times(1)).createInvoice(any());
	verify(billingEventPublisher, times(1)).publishSubscriptionCreated(any());
	verify(billingEventPublisher, times(1)).publishInvoiceRequested(any());
	verify(billingEventPublisher, times(1)).publishPaymentFailed(any(), anyString());
	verify(billingEventPublisher, times(1)).publishSupportTicketRaised(any(), anyString());
    }

    @Test
    void activateSubscriptionAllowsTransitionFromSuspended() {
	Subscription subscription = new Subscription();
	subscription.setId(10L);
	subscription.setStatus(SubscriptionStatus.SUSPENDED);

	when(subscriptionRepository.findById(10L)).thenReturn(Optional.of(subscription));
	when(subscriptionRepository.save(any(Subscription.class))).thenAnswer(invocation -> invocation.getArgument(0));

	Subscription updated = subscriptionService.activateSubscription(10L);

	assertEquals(SubscriptionStatus.ACTIVE, updated.getStatus());
    }

    @Test
    void cancelSubscriptionAllowsAnyNonCancelledState() {
	Subscription subscription = new Subscription();
	subscription.setId(11L);
	subscription.setStatus(SubscriptionStatus.ACTIVE);

	when(subscriptionRepository.findById(11L)).thenReturn(Optional.of(subscription));
	when(subscriptionRepository.save(any(Subscription.class))).thenAnswer(invocation -> invocation.getArgument(0));

	Subscription updated = subscriptionService.cancelSubscription(11L);

	assertEquals(SubscriptionStatus.CANCELLED, updated.getStatus());
    }

    @Test
    void markPaymentFailedRaisesEvents() {
	Subscription subscription = new Subscription();
	subscription.setId(12L);
	subscription.setStatus(SubscriptionStatus.ACTIVE);

	when(subscriptionRepository.findById(12L)).thenReturn(Optional.of(subscription));
	when(subscriptionRepository.save(any(Subscription.class))).thenAnswer(invocation -> invocation.getArgument(0));

	Subscription updated = subscriptionService.markPaymentFailed(12L);

	assertEquals(SubscriptionStatus.PAYMENT_FAILED, updated.getStatus());
	verify(billingEventPublisher, times(1)).publishPaymentFailed(any(), anyString());
	verify(billingEventPublisher, times(1)).publishSupportTicketRaised(any(), anyString());
    }
}
