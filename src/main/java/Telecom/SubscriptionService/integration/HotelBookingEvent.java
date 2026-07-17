package Telecom.SubscriptionService.integration;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class HotelBookingEvent implements Serializable {
    private String bookingId;
    private Long userId;
    private String status;
}
