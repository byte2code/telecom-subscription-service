package Telecom.SubscriptionService.repository;


import org.springframework.data.jpa.repository.JpaRepository;

import Telecom.SubscriptionService.model.User;

public interface UserRepository extends JpaRepository<User, Long> {
    User findByName(String name);
    User findByEmail(String email);
}

