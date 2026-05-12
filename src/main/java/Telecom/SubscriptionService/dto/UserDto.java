package Telecom.SubscriptionService.dto;



import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import Telecom.SubscriptionService.model.Account;
import Telecom.SubscriptionService.model.Subscription;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {
    private String name;
    private String email;
    private BigInteger contact;
    private String address;
    private Account account;
    private List<Subscription> subscriptionList = new ArrayList<>();
}

