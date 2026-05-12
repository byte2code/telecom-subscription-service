package Telecom.SubscriptionService.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import Telecom.SubscriptionService.feign.BillingService;
import Telecom.SubscriptionService.feign.SupportService;
import Telecom.SubscriptionService.dto.SubscriptionDto;
import Telecom.SubscriptionService.model.Subscription;
import Telecom.SubscriptionService.model.User;
import Telecom.SubscriptionService.repository.SubscriptionRepository;
import Telecom.SubscriptionService.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class SubscriptionService {

    private final SubscriptionRepository subscriptionRepository;
    private final UserRepository userRepository;

    // Feign clients (must exist under @EnableFeignClients scan)
    private final BillingService billingService;
    private final SupportService supportService;

    public List<Subscription> getAllSubscriptions() {
	return subscriptionRepository.findAll();
    }

    public Subscription getSubscriptionById(Long id) {
	return subscriptionRepository.findById(id).orElse(null);
    }

    public List<Subscription> getSubscriptionsByUserId(Long userId) {
	User user = userRepository.findById(userId).orElse(null);
	return user != null ? user.getSubscriptionList() : List.of();
    }

    public void updateSubscription(Long id, SubscriptionDto dto) {
	subscriptionRepository.findById(id).ifPresent(subscription -> {
	    subscription.setPrice(dto.getPrice());
	    subscription.setPlanName(dto.getPlanName());
	    subscription.setPlanDetails(dto.getPlanDetails());
	    subscriptionRepository.save(subscription);
	});
    }

    // Save subscription and delegate invoice creation to billing-service via Feign
    public void createSubscription(SubscriptionDto dto) {
	userRepository.findById(dto.getUserId()).ifPresent(user -> {
	    Subscription subscription = new Subscription();
	    subscription.setPrice(dto.getPrice());
	    subscription.setPlanName(dto.getPlanName());
	    subscription.setPlanDetails(dto.getPlanDetails());
	    subscription.setUser(user);
	    subscriptionRepository.save(subscription);

	    // Build invoice payload for billing-service
	    Map<String, Object> invoice = new HashMap<>();
	    invoice.put("userId", dto.getUserId());
	    invoice.put("price", dto.getPrice());
	    invoice.put("planName", dto.getPlanName());

	    // Feign call replaces RestTemplate
	    billingService.createInvoice(invoice);
	});
    }

    public void deleteSubscription(Long id) {
	subscriptionRepository.deleteById(id);
    }

    // Feign-backed tickets retrieval to replace any RestTemplate call
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
