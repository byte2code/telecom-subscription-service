package Telecom.SubscriptionService.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.databind.ObjectMapper;

import Telecom.SubscriptionService.feign.SupportService;
import Telecom.SubscriptionService.model.User;
import Telecom.SubscriptionService.repository.UserRepository;

class UserServiceTest {

    private UserRepository userRepository;

    private SupportService supportService;

    private UserService userService;

    @BeforeEach
    void setUp() {
	userRepository = mock(UserRepository.class);
	supportService = mock(SupportService.class);
	userService = new UserService(userRepository, supportService, new ObjectMapper());
    }

    @Test
    void getUserTicketsParsesSupportPayload() throws Exception {
	when(supportService.getTickets(7L)).thenReturn("[{\"id\":1,\"subject\":\"Billing issue\"}]");

	List<Object> tickets = userService.getUserTickets(7L).get();

	assertEquals(1, tickets.size());
    }

    @Test
    void getUserTicketsFallbackReturnsHelpfulMessage() throws Exception {
	List<Object> fallback = userService.getUserTicketsFallback(7L, new RuntimeException("timeout")).get();

	assertEquals(1, fallback.size());
	assertTrue(fallback.get(0).toString().contains("Tickets unavailable"));
    }
}
