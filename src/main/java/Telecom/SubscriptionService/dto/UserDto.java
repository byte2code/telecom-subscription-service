package Telecom.SubscriptionService.dto;



import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import Telecom.SubscriptionService.model.Account;
import Telecom.SubscriptionService.model.Subscription;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {

    @NotBlank(message = "Name must not be blank")
    private String name;

    @NotBlank(message = "Email must not be blank")
    @Email(message = "Email must be a valid email address")
    private String email;

    @NotNull(message = "Contact number is required")
    private BigInteger contact;

    private String address;
    private Account account;
    private List<Subscription> subscriptionList = new ArrayList<>();
}
