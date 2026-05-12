package Telecom.SubscriptionService.service;

import java.util.List;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;

import Telecom.SubscriptionService.dto.UserDto;
import Telecom.SubscriptionService.model.User;
import Telecom.SubscriptionService.repository.UserRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final RestTemplate restTemplate; // Ensure RestTemplate bean defined in config

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

//    public List<Object> getUserTickets(Long userId) {
//        String url = "http://localhost:8083/"; // align with test stub
//        ResponseEntity<List<Object>> response = restTemplate.exchange(
//            url, HttpMethod.GET, null, new ParameterizedTypeReference<List<Object>>() {});
//        return response.getBody() != null ? response.getBody() : java.util.Collections.emptyList();
//    }
//    
    @HystrixCommand(fallbackMethod = "getUserTicketsFallback")
    public List<Object> getUserTickets(Long userId) {
	String url = "http://localhost:8083/tickets/" + userId; // align with tests/mock
	ResponseEntity<List<Object>> resp = restTemplate.exchange(url, HttpMethod.GET, null,
		new ParameterizedTypeReference<List<Object>>() {
		});
	return resp.getBody() != null ? resp.getBody() : java.util.Collections.emptyList();
    }

    // Fallback signature: same params (+ optional Throwable)
    public List<Object> getUserTicketsFallback(Long userId, Throwable t) {
	return java.util.List.of(java.util.Map.of("message", "Tickets unavailable (fallback)"));
    }

}
