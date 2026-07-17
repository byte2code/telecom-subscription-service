package Telecom.SubscriptionService.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import Telecom.SubscriptionService.model.Invoice;

public interface InvoiceRepository extends JpaRepository<Invoice, Long> {
    List<Invoice> findByUserId(Long userId);
}
