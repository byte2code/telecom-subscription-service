package Telecom.SubscriptionService.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;

import Telecom.SubscriptionService.dto.ResponseMessage;
import Telecom.SubscriptionService.dto.UserDto;
import Telecom.SubscriptionService.model.User;
import Telecom.SubscriptionService.service.UserService;

@RestController
@RequestMapping({ "/api/user", "" })
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
	this.userService = userService;
    }

    @GetMapping
    public ResponseEntity<List<User>> getAllUsers() {
	return ResponseEntity.ok(userService.getAllUsers());
    }

    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable Long id) {
	return ResponseEntity.ok(userService.getUserById(id));
    }

    @PostMapping
    public ResponseEntity<ResponseMessage> createUser(@RequestBody UserDto userDto) {
	userService.createUser(userDto);
	return new ResponseEntity<>(new ResponseMessage("User created Successfully"), HttpStatus.CREATED);
    }

    @GetMapping("/name/{name}")
    public ResponseEntity<User> getUserByName(@PathVariable String name) {
	return ResponseEntity.ok(userService.getUserByName(name));
    }

    @GetMapping("/email/{email}")
    public ResponseEntity<User> getUserByEmail(@PathVariable String email) {
	return ResponseEntity.ok(userService.getUserByEmail(email));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ResponseMessage> updateUser(@PathVariable Long id, @RequestBody UserDto userDto) {
	userService.updateUser(id, userDto);
	return ResponseEntity.ok(new ResponseMessage("User updated Successfully"));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ResponseMessage> deleteUser(@PathVariable Long id) {
	userService.deleteUser(id);
	return ResponseEntity.ok(new ResponseMessage("User Deleted Successfully"));
    }

 // Exposed at both /tickets/{userId} and /api/user/tickets/{userId}
//    @GetMapping("/tickets/{userId}")
//    public ResponseEntity<List<Object>> getUserTickets(@PathVariable Long userId) {
//        return ResponseEntity.ok(userService.getUserTickets(userId));
//    }
    
    @GetMapping("/tickets/{userId}")
    @HystrixCommand(fallbackMethod = "getUserTicketsFallback")
    public ResponseEntity<List<Object>> getUserTickets(@PathVariable Long userId) {
      return ResponseEntity.ok(userService.getUserTickets(userId));
    }
    
    public ResponseEntity<List<Object>> getUserTicketsFallback(@PathVariable Long userId, Throwable t) {
	  return ResponseEntity.ok(java.util.List.of(java.util.Map.of("message", "Tickets unavailable (fallback)")));
	}
}
