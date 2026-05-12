package Telecom.SubscriptionService.dto;


import lombok.Data;
import lombok.NoArgsConstructor;
import Telecom.SubscriptionService.model.User;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AccountDto {
    private User user;
    private String balance;
    private String details;
}

