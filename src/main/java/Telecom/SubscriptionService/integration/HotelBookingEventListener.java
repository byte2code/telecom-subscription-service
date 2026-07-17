package Telecom.SubscriptionService.integration;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import Telecom.SubscriptionService.dto.SubscriptionDto;
import Telecom.SubscriptionService.model.Plan;
import Telecom.SubscriptionService.repository.PlanRepository;
import Telecom.SubscriptionService.service.SubscriptionService;
import Telecom.SubscriptionService.messaging.BillingRabbitConfig;

@Slf4j
@Service
@RequiredArgsConstructor
public class HotelBookingEventListener {

    private final SubscriptionService subscriptionService;
    private final PlanRepository planRepository;

    @RabbitListener(queues = BillingRabbitConfig.HOTEL_EVENTS_QUEUE)
    @Transactional
    public void handleHotelBooking(HotelBookingEvent event) {
        if ("CONFIRMED".equalsIgnoreCase(event.getStatus())) {
            log.info("Received confirmed hotel booking for userId={}, provisioning WiFi plan...", event.getUserId());
            
            // Find or create a default WiFi plan
            Plan wifiPlan = planRepository.findAll().stream()
                .filter(p -> "Hotel WiFi Pass".equals(p.getName()))
                .findFirst()
                .orElseGet(() -> {
                    Plan newPlan = new Plan();
                    newPlan.setName("Hotel WiFi Pass");
                    newPlan.setPrice(0); // Included in hotel booking
                    return planRepository.save(newPlan);
                });

            SubscriptionDto dto = new SubscriptionDto();
            dto.setUserId(event.getUserId());
            dto.setPlanId(wifiPlan.getId());
            
            subscriptionService.createSubscription(dto);
            log.info("Successfully provisioned WiFi plan for userId={}", event.getUserId());
        }
    }
}
