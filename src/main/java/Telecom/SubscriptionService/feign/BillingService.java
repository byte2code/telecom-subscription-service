package Telecom.SubscriptionService.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

//Feign client for the billing service
@FeignClient(name = "billing-service")
public interface BillingService {

    @PostMapping("/api/billing/invoices")
    String createInvoice(@RequestBody Object invoiceRequest);

    @GetMapping("/api/billing/invoices/{id}")
    String getInvoice(@PathVariable("id") Long id);
}
