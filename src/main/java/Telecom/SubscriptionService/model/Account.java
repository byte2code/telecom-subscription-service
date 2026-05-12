package Telecom.SubscriptionService.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import javax.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Entity
@Data
@NoArgsConstructor
public class Account implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String balance;
    private String details;

    @OneToOne
    @JoinColumn(name = "user_id")
    @JsonIgnoreProperties("account")
    private User user;
}
