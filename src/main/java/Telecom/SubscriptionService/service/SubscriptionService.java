package Telecom.SubscriptionService.service;


import java.util.List;

import org.springframework.stereotype.Service;

import Telecom.SubscriptionService.dto.SubscriptionDto;
import Telecom.SubscriptionService.model.Subscription;
import Telecom.SubscriptionService.model.User;
import Telecom.SubscriptionService.repository.SubscriptionRepository;
import Telecom.SubscriptionService.repository.UserRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SubscriptionService {

    private final SubscriptionRepository subscriptionRepository;
    private final UserRepository userRepository;

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

    public void createSubscription(SubscriptionDto dto) {
        userRepository.findById(dto.getUserId()).ifPresent(user -> {
            Subscription subscription = new Subscription();
            subscription.setPrice(dto.getPrice());
            subscription.setPlanName(dto.getPlanName());
            subscription.setPlanDetails(dto.getPlanDetails());
            subscription.setUser(user);
            subscriptionRepository.save(subscription);
        });
    }

    public void deleteSubscription(Long id) {
        subscriptionRepository.deleteById(id);
    }
}

