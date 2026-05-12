package Telecom.SubscriptionService.service;


import java.util.List;

import org.springframework.stereotype.Service;

import Telecom.SubscriptionService.dto.UserDto;
import Telecom.SubscriptionService.model.User;
import Telecom.SubscriptionService.repository.UserRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

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
}
