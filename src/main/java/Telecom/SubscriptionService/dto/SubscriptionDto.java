package Telecom.SubscriptionService.dto;


import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SubscriptionDto {

    @NotNull(message = "Price is required")
    @Positive(message = "Price must be a positive number")
    private Integer price;

    @NotBlank(message = "Plan name must not be blank")
    private String planName;

    private String planDetails;

    @NotNull(message = "User ID is required")
    private Long userId;
}
