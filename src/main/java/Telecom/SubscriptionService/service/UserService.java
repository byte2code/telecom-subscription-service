package Telecom.SubscriptionService.service;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import io.github.resilience4j.timelimiter.annotation.TimeLimiter;
import Telecom.SubscriptionService.dto.UserDto;
import Telecom.SubscriptionService.feign.SupportService;
import Telecom.SubscriptionService.model.User;
import Telecom.SubscriptionService.repository.UserRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final SupportService supportService;
    private final ObjectMapper objectMapper;

    public List<User> getAllUsers() {
	return userRepository.findAll();
    }

    public User getUserById(Long id) {
	return userRepository.findById(id).orElse(null);
    }

    public void createUser(UserDto userDto) {
	User user = new User();
	user.setName(userDto.getName());
	user.setEmail(userDto.getEmail());
	user.setContact(userDto.getContact());
	user.setAddress(userDto.getAddress());
	user.setAccount(userDto.getAccount());
	user.setSubscriptionList(userDto.getSubscriptionList());
	userRepository.save(user);
    }

    public User getUserByName(String name) {
	return userRepository.findByName(name);
    }

    public User getUserByEmail(String email) {
	return userRepository.findByEmail(email);
    }

    public void updateUser(Long id, UserDto userDto) {
	userRepository.findById(id).ifPresent(user -> {
	    user.setName(userDto.getName());
	    user.setEmail(userDto.getEmail());
	    user.setContact(userDto.getContact());
	    user.setAddress(userDto.getAddress());
	    userRepository.save(user);
	});
    }

    public void deleteUser(Long id) {
	userRepository.deleteById(id);
    }

    @CircuitBreaker(name = "supportTickets", fallbackMethod = "getUserTicketsFallback")
    @Retry(name = "supportTickets")
    @TimeLimiter(name = "supportTickets")
    public CompletableFuture<List<Object>> getUserTickets(Long userId) {
	return CompletableFuture.supplyAsync(() -> {
	    String payload = supportService.getTickets(userId);
	    try {
		List<Object> tickets = objectMapper.readValue(payload, new TypeReference<List<Object>>() {
		});
		return tickets != null ? tickets : Collections.emptyList();
	    } catch (Exception ex) {
		throw new IllegalStateException("Failed to parse support tickets response", ex);
	    }
	});
    }

    // Fallback signature: same params (+ optional Throwable)
    public CompletableFuture<List<Object>> getUserTicketsFallback(Long userId, Throwable t) {
	return CompletableFuture.completedFuture(List.of(Map.of("message", "Tickets unavailable (fallback)")));
    }

}
