package Telecom.SubscriptionService.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import Telecom.SubscriptionService.model.Invoice;
import Telecom.SubscriptionService.repository.InvoiceRepository;

@RestController
@RequestMapping("/api/invoices")
@RequiredArgsConstructor
public class InvoiceController {

    private final InvoiceRepository invoiceRepository;

    @GetMapping("/{userId}")
    public ResponseEntity<List<Invoice>> getInvoicesByUserId(@PathVariable Long userId) {
        return ResponseEntity.ok(invoiceRepository.findByUserId(userId));
    }
}
