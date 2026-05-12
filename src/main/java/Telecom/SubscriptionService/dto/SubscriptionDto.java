package Telecom.SubscriptionService.dto;


import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SubscriptionDto {
    private Integer price;
    private String planName;
    private String planDetails;
    private Long userId;
}

