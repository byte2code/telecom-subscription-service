package Telecom.SubscriptionService.repository;


import org.springframework.data.jpa.repository.JpaRepository;

import Telecom.SubscriptionService.model.Subscription;

public interface SubscriptionRepository extends JpaRepository<Subscription, Long> {
}
