package Telecom.SubscriptionService.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import javax.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Entity
@Data
@NoArgsConstructor
public class Subscription implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Integer price;
    private String planName;
    private String planDetails;

    @ManyToOne
    @JoinColumn(name = "user_id")
    @JsonIgnoreProperties("subscriptionList")
    private User user;
}
