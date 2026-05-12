package Telecom.SubscriptionService.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

// Feign client for the support service
@FeignClient(name = "support-service")
public interface SupportService {

    @PostMapping("/api/support/tickets")
    String createTicket(@RequestBody Object ticketRequest);

    @GetMapping("/api/support/tickets/{userId}")
    String getTickets(@PathVariable("userId") Long userId);
}
