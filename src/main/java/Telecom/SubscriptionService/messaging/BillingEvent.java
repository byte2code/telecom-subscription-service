package Telecom.SubscriptionService.messaging;

import java.io.Serializable;
import java.time.Instant;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BillingEvent implements Serializable {

    private BillingEventType eventType;
    private Long subscriptionId;
    private Long userId;
    private String planName;
    private Integer amount;
    private String message;
    private String source;
    private Instant createdAt;
}
