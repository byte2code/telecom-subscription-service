package Telecom.SubscriptionService.repository;



import org.springframework.data.jpa.repository.JpaRepository;

import Telecom.SubscriptionService.model.Account;

public interface AccountRepository extends JpaRepository<Account, Long> {
}

