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
import org.springframework.web.client.RestTemplate;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;

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
    private final RestTemplate restTemplate;

    public SubscriptionController(SubscriptionService subscriptionService, UserService userService,
	    RestTemplate restTemplate) {
	this.subscriptionService = subscriptionService;
	this.userService = userService;
	this.restTemplate = restTemplate;
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
    @HystrixCommand(fallbackMethod = "createSubscriptionFallback")
    public ResponseEntity<ResponseMessage> createSubscription(@RequestBody SubscriptionDto dto) {
	User user = userService.getUserById(dto.getUserId());
	if (user == null) {
	    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
		    .body(new ResponseMessage("User not found with ID: " + dto.getUserId()));
	}
	subscriptionService.createSubscription(dto);
	var invoice = new java.util.HashMap<String, Object>();
	invoice.put("userId", dto.getUserId());
	invoice.put("price", dto.getPrice());
	invoice.put("planName", dto.getPlanName());
	restTemplate.postForEntity("http://localhost:8082/", invoice, String.class);
	return new ResponseEntity<>(new ResponseMessage("Subscription Created Successfully"), HttpStatus.CREATED);
    }

    public ResponseEntity<ResponseMessage> createSubscriptionFallback(SubscriptionDto dto, Throwable t) {
	return new ResponseEntity<>(new ResponseMessage("Subscription temporarily unavailable (fallback)"),
		HttpStatus.CREATED);
    }

}
