package Telecom.SubscriptionService.integration;

import Telecom.SubscriptionService.dto.SubscriptionDto;
import Telecom.SubscriptionService.feign.BillingService;
import Telecom.SubscriptionService.feign.SupportService;
import Telecom.SubscriptionService.model.Plan;
import Telecom.SubscriptionService.model.Subscription;
import Telecom.SubscriptionService.model.SubscriptionStatus;
import Telecom.SubscriptionService.model.User;
import Telecom.SubscriptionService.repository.PlanRepository;
import Telecom.SubscriptionService.repository.SubscriptionRepository;
import Telecom.SubscriptionService.repository.UserRepository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.containers.RabbitMQContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
public class SubscriptionLifecycleIntegrationTest {

    @Container
    static final MySQLContainer<?> mySQLContainer = new MySQLContainer<>("mysql:8.0")
            .withDatabaseName("telecom_test")
            .withUsername("test")
            .withPassword("test");

    @Container
    static final RabbitMQContainer rabbitMQContainer = new RabbitMQContainer("rabbitmq:3.12-management");

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", mySQLContainer::getJdbcUrl);
        registry.add("spring.datasource.username", mySQLContainer::getUsername);
        registry.add("spring.datasource.password", mySQLContainer::getPassword);
        registry.add("spring.rabbitmq.host", rabbitMQContainer::getHost);
        registry.add("spring.rabbitmq.port", rabbitMQContainer::getAmqpPort);
        registry.add("eureka.client.enabled", () -> false);
        registry.add("spring.cloud.discovery.enabled", () -> false);
        // Disable security for integration tests so TestRestTemplate isn't blocked by 401 Unauthorized
        registry.add("spring.autoconfigure.exclude", () -> "org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration,org.springframework.boot.actuate.autoconfigure.security.servlet.ManagementWebSecurityAutoConfiguration,org.springframework.boot.autoconfigure.security.oauth2.resource.servlet.OAuth2ResourceServerAutoConfiguration");
    }

    @MockBean
    private BillingService billingService;

    @MockBean
    private SupportService supportService;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private SubscriptionRepository subscriptionRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PlanRepository planRepository;

    @Test
    void subscriptionLifecycleHappyPath() {
        // a. Save a User
        User user = new User();
        user.setName("Integration User");
        user.setEmail("integration@test.com");
        user = userRepository.save(user);

        Plan plan = new Plan();
        plan.setName("Integration Plan");
        plan.setPrice(500);
        plan = planRepository.save(plan);

        // b. POST a SubscriptionDto to /api/subscription
        when(billingService.createInvoice(any())).thenReturn("ok");

        SubscriptionDto dto = new SubscriptionDto();
        dto.setUserId(user.getId());
        dto.setPlanId(plan.getId());

        ResponseEntity<String> createResponse = restTemplate.postForEntity("/api/subscription", dto, String.class);

        // c. Assert response is 201 and body message contains "ACTIVE"
        assertEquals(HttpStatus.CREATED, createResponse.getStatusCode());
        assertTrue(createResponse.getBody() != null && createResponse.getBody().contains("ACTIVE"), "Expected ACTIVE in response");

        // d. Get the created subscription id from subscriptionRepository
        Subscription subscription = subscriptionRepository.findAll().get(0);
        Long id = subscription.getId();

        // e. POST to /api/subscription/{id}/suspend
        ResponseEntity<String> suspendResponse = restTemplate.postForEntity("/api/subscription/" + id + "/suspend", null, String.class);

        // f. Assert response is 200 and subscription status in DB is SUSPENDED
        assertEquals(HttpStatus.OK, suspendResponse.getStatusCode());
        Subscription suspendedSub = subscriptionRepository.findById(id).orElseThrow();
        assertEquals(SubscriptionStatus.SUSPENDED, suspendedSub.getStatus());

        // g. POST to /api/subscription/{id}/activate
        ResponseEntity<String> activateResponse = restTemplate.postForEntity("/api/subscription/" + id + "/activate", null, String.class);

        // h. Assert response is 200 and status in DB is ACTIVE
        assertEquals(HttpStatus.OK, activateResponse.getStatusCode());
        Subscription activeSub = subscriptionRepository.findById(id).orElseThrow();
        assertEquals(SubscriptionStatus.ACTIVE, activeSub.getStatus());

        // i. POST to /api/subscription/{id}/cancel
        ResponseEntity<String> cancelResponse = restTemplate.postForEntity("/api/subscription/" + id + "/cancel", null, String.class);

        // j. Assert response is 200 and status in DB is CANCELLED
        assertEquals(HttpStatus.OK, cancelResponse.getStatusCode());
        Subscription cancelledSub = subscriptionRepository.findById(id).orElseThrow();
        assertEquals(SubscriptionStatus.CANCELLED, cancelledSub.getStatus());
    }
}
