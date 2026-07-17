package Telecom.SubscriptionService.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import Telecom.SubscriptionService.model.Plan;

public interface PlanRepository extends JpaRepository<Plan, Long> {
}
