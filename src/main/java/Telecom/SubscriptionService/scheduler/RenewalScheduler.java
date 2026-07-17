package Telecom.SubscriptionService.scheduler;

import java.time.LocalDate;
import java.util.List;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import Telecom.SubscriptionService.messaging.BillingEventPublisher;
import Telecom.SubscriptionService.model.Subscription;
import Telecom.SubscriptionService.model.SubscriptionStatus;
import Telecom.SubscriptionService.repository.SubscriptionRepository;

@Component
@RequiredArgsConstructor
@Slf4j
public class RenewalScheduler {

    private final SubscriptionRepository subscriptionRepository;
    private final BillingEventPublisher billingEventPublisher;

    @Scheduled(cron = "0 0 0 * * ?") // Run daily at midnight
    public void processRenewals() {
        log.info("Starting daily renewal job");
        List<Subscription> subscriptions = subscriptionRepository.findAll();
        
        LocalDate today = LocalDate.now();
        int renewedCount = 0;

        for (Subscription sub : subscriptions) {
            if (sub.getStatus() == SubscriptionStatus.ACTIVE 
                && sub.getNextRenewalDate() != null 
                && !sub.getNextRenewalDate().isAfter(today)) {
                
                log.info("Publishing renewal requested for subscription ID: {}", sub.getId());
                billingEventPublisher.publishRenewalRequested(sub);
                
                // Update next renewal date
                sub.setNextRenewalDate(today.plusDays(30));
                subscriptionRepository.save(sub);
                renewedCount++;
            }
        }
        
        log.info("Completed daily renewal job. Renewed {} subscriptions.", renewedCount);
    }
}
