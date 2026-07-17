package Telecom.SubscriptionService.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import javax.validation.Valid;

import Telecom.SubscriptionService.dto.ResponseMessage;
import Telecom.SubscriptionService.dto.SubscriptionDto;
import Telecom.SubscriptionService.model.Subscription;
import Telecom.SubscriptionService.model.User;
import Telecom.SubscriptionService.service.SubscriptionService;
import Telecom.SubscriptionService.service.UserService;

@RestController
@RequestMapping("/api/subscription")
public class SubscriptionController {

    private final SubscriptionService subscriptionService;
    private final UserService userService;

    public SubscriptionController(SubscriptionService subscriptionService, UserService userService) {
	this.subscriptionService = subscriptionService;
	this.userService = userService;
    }

    @GetMapping
    public ResponseEntity<List<Subscription>> getAllSubscriptions() {
	return ResponseEntity.ok(subscriptionService.getAllSubscriptions());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Subscription> getSubscriptionById(@PathVariable Long id) {
	return ResponseEntity.ok(subscriptionService.getSubscriptionById(id));
    }

    @GetMapping("/userId/{userId}")
    public ResponseEntity<List<Subscription>> getSubscriptionsByUserId(@PathVariable Long userId) {
	return ResponseEntity.ok(subscriptionService.getSubscriptionsByUserId(userId));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ResponseMessage> deleteSubscription(@PathVariable Long id) {
	subscriptionService.deleteSubscription(id);
	return ResponseEntity.ok(new ResponseMessage("Subscription Deleted Successfully"));
    }

    @PostMapping
    public ResponseEntity<ResponseMessage> createSubscription(@Valid @RequestBody SubscriptionDto dto) {
	User user = userService.getUserById(dto.getUserId());
	if (user == null) {
	    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
		    .body(new ResponseMessage("User not found with ID: " + dto.getUserId()));
	}
	Subscription created = subscriptionService.createSubscription(dto);
	if (created == null) {
	    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
		    .body(new ResponseMessage("Subscription could not be created"));
	}
	return new ResponseEntity<>(
		new ResponseMessage("Subscription created with status " + created.getStatus()),
		HttpStatus.CREATED);
    }

    @PostMapping("/{id}/activate")
    public ResponseEntity<ResponseMessage> activateSubscription(@PathVariable Long id) {
	try {
	    Subscription subscription = subscriptionService.activateSubscription(id);
	    if (subscription == null) {
		return ResponseEntity.status(HttpStatus.NOT_FOUND)
			.body(new ResponseMessage("Subscription not found with ID: " + id));
	    }
	    return ResponseEntity.ok(new ResponseMessage("Subscription marked ACTIVE"));
	} catch (IllegalStateException ex) {
	    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseMessage(ex.getMessage()));
	}
    }

    @PostMapping("/{id}/suspend")
    public ResponseEntity<ResponseMessage> suspendSubscription(@PathVariable Long id) {
	try {
	    Subscription subscription = subscriptionService.suspendSubscription(id);
	    if (subscription == null) {
		return ResponseEntity.status(HttpStatus.NOT_FOUND)
			.body(new ResponseMessage("Subscription not found with ID: " + id));
	    }
	    return ResponseEntity.ok(new ResponseMessage("Subscription marked SUSPENDED"));
	} catch (IllegalStateException ex) {
	    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseMessage(ex.getMessage()));
	}
    }

    @PostMapping("/{id}/cancel")
    public ResponseEntity<ResponseMessage> cancelSubscription(@PathVariable Long id) {
	try {
	    Subscription subscription = subscriptionService.cancelSubscription(id);
	    if (subscription == null) {
		return ResponseEntity.status(HttpStatus.NOT_FOUND)
			.body(new ResponseMessage("Subscription not found with ID: " + id));
	    }
	    return ResponseEntity.ok(new ResponseMessage("Subscription marked CANCELLED"));
	} catch (IllegalStateException ex) {
	    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseMessage(ex.getMessage()));
	}
    }

    @PostMapping("/{id}/payment-failed")
    public ResponseEntity<ResponseMessage> markPaymentFailed(@PathVariable Long id) {
	try {
	    Subscription subscription = subscriptionService.markPaymentFailed(id);
	    if (subscription == null) {
		return ResponseEntity.status(HttpStatus.NOT_FOUND)
			.body(new ResponseMessage("Subscription not found with ID: " + id));
	    }
	    return ResponseEntity.ok(new ResponseMessage("Subscription marked PAYMENT_FAILED"));
	} catch (IllegalStateException ex) {
	    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseMessage(ex.getMessage()));
	}
    }

}
