package Telecom.SubscriptionService.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import Telecom.SubscriptionService.feign.BillingService;
import Telecom.SubscriptionService.feign.SupportService;
import Telecom.SubscriptionService.dto.SubscriptionDto;
import Telecom.SubscriptionService.messaging.BillingEventPublisher;
import Telecom.SubscriptionService.model.Subscription;
import Telecom.SubscriptionService.model.SubscriptionStatus;
import Telecom.SubscriptionService.model.User;
import Telecom.SubscriptionService.model.Plan;
import Telecom.SubscriptionService.repository.PlanRepository;
import Telecom.SubscriptionService.repository.SubscriptionRepository;
import Telecom.SubscriptionService.repository.UserRepository;
import java.time.LocalDate;

@Service
@RequiredArgsConstructor
@Transactional
public class SubscriptionService {

    private final SubscriptionRepository subscriptionRepository;
    private final UserRepository userRepository;
    private final PlanRepository planRepository;

    // Feign clients (must exist under @EnableFeignClients scan)
    private final BillingService billingService;
    private final SupportService supportService;
    private final BillingEventPublisher billingEventPublisher;

    @Transactional(readOnly = true)
    public List<Subscription> getAllSubscriptions() {
	return subscriptionRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Subscription getSubscriptionById(Long id) {
	return subscriptionRepository.findById(id).orElse(null);
    }

    @Transactional(readOnly = true)
    public List<Subscription> getSubscriptionsByUserId(Long userId) {
	User user = userRepository.findById(userId).orElse(null);
	return user != null ? user.getSubscriptionList() : List.of();
    }

    public void updateSubscription(Long id, SubscriptionDto dto) {
	subscriptionRepository.findById(id).ifPresent(subscription -> {
	    Plan plan = planRepository.findById(dto.getPlanId()).orElse(null);
	    if (plan != null) {
	        subscription.setPlan(plan);
	    }
	    subscriptionRepository.save(subscription);
	});
    }

    // Save subscription in REQUESTED state and move it through billing-driven lifecycle.
    public Subscription createSubscription(SubscriptionDto dto) {
	User user = userRepository.findById(dto.getUserId()).orElse(null);
	Plan plan = planRepository.findById(dto.getPlanId()).orElse(null);
	if (user == null || plan == null) {
	    return null;
	}

	Subscription subscription = new Subscription();
	subscription.setPlan(plan);
	subscription.setUser(user);
	subscription.setStatus(SubscriptionStatus.REQUESTED);
	subscription.setNextRenewalDate(LocalDate.now().plusDays(30)); // default 30 days billing cycle
	subscription = subscriptionRepository.save(subscription);
	billingEventPublisher.publishSubscriptionCreated(subscription);

	Map<String, Object> invoice = new HashMap<>();
	invoice.put("userId", dto.getUserId());
	invoice.put("price", plan.getPrice());
	invoice.put("planName", plan.getName());
	invoice.put("subscriptionId", subscription.getId());
	billingEventPublisher.publishInvoiceRequested(subscription);

	try {
	    billingService.createInvoice(invoice);
	    subscription.setStatus(SubscriptionStatus.ACTIVE);
	} catch (Exception ex) {
	    subscription.setStatus(SubscriptionStatus.PAYMENT_FAILED);
	    billingEventPublisher.publishPaymentFailed(subscription,
		    "Billing failed while creating invoice: " + ex.getMessage());
	    billingEventPublisher.publishSupportTicketRaised(subscription,
		    "Support ticket raised because billing failed for subscription " + subscription.getId());
	}

	return subscriptionRepository.save(subscription);
    }

    public Subscription activateSubscription(Long id) {
	return transitionSubscriptionStatus(id, SubscriptionStatus.ACTIVE);
    }

    public Subscription suspendSubscription(Long id) {
	return transitionSubscriptionStatus(id, SubscriptionStatus.SUSPENDED);
    }

    public Subscription cancelSubscription(Long id) {
	return transitionSubscriptionStatus(id, SubscriptionStatus.CANCELLED);
    }

    public Subscription markPaymentFailed(Long id) {
	Subscription subscription = transitionSubscriptionStatus(id, SubscriptionStatus.PAYMENT_FAILED);
	if (subscription != null) {
	    billingEventPublisher.publishPaymentFailed(subscription,
		    "Subscription manually marked as PAYMENT_FAILED");
	    billingEventPublisher.publishSupportTicketRaised(subscription,
		    "Support ticket raised because subscription " + subscription.getId() + " moved to PAYMENT_FAILED");
	}
	return subscription;
    }

    private Subscription transitionSubscriptionStatus(Long id, SubscriptionStatus targetStatus) {
	Subscription subscription = subscriptionRepository.findById(id).orElse(null);
	if (subscription == null) {
	    return null;
	}

	SubscriptionStatus currentStatus = subscription.getStatus();
	if (!isValidTransition(currentStatus, targetStatus)) {
	    throw new IllegalStateException(
		    "Cannot change subscription status from " + currentStatus + " to " + targetStatus);
	}

	subscription.setStatus(targetStatus);
	return subscriptionRepository.save(subscription);
    }

    private boolean isValidTransition(SubscriptionStatus currentStatus, SubscriptionStatus targetStatus) {
	if (currentStatus == null) {
	    currentStatus = SubscriptionStatus.REQUESTED;
	}

	if (currentStatus == targetStatus) {
	    return true;
	}

	switch (targetStatus) {
	case ACTIVE:
	    return currentStatus == SubscriptionStatus.REQUESTED || currentStatus == SubscriptionStatus.PAYMENT_FAILED
		    || currentStatus == SubscriptionStatus.SUSPENDED;
	case SUSPENDED:
	    return currentStatus == SubscriptionStatus.ACTIVE;
	case CANCELLED:
	    return currentStatus != SubscriptionStatus.CANCELLED;
	case PAYMENT_FAILED:
	    return currentStatus == SubscriptionStatus.REQUESTED || currentStatus == SubscriptionStatus.ACTIVE;
	case REQUESTED:
	    return false;
	default:
	    return false;
	}
    }

    public void deleteSubscription(Long id) {
	subscriptionRepository.deleteById(id);
    }

    // Feign-backed tickets retrieval for the support-service fallback path
    public List<Object> getUserTickets(Long userId) {
	String json = supportService.getTickets(userId);
	try {
	    return new com.fasterxml.jackson.databind.ObjectMapper().readValue(json,
		    new com.fasterxml.jackson.core.type.TypeReference<List<Object>>() {
		    });
	} catch (Exception e) {
	    return java.util.Collections.emptyList();
	}
    }

}
