package Telecom.SubscriptionService;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.containers.RabbitMQContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import Telecom.SubscriptionService.dto.SubscriptionDto;
import Telecom.SubscriptionService.feign.BillingService;
import Telecom.SubscriptionService.model.Plan;
import Telecom.SubscriptionService.model.Subscription;
import Telecom.SubscriptionService.repository.PlanRepository;
import Telecom.SubscriptionService.service.SubscriptionService;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@SpringBootTest
@Testcontainers
class TelecomIntegrationTest {

    @Container
    static final MySQLContainer<?> mySQLContainer = new MySQLContainer<>("mysql:8.0")
            .withDatabaseName("telecom_test")
            .withUsername("user")
            .withPassword("password");

    @Container
    static final RabbitMQContainer rabbitMQContainer = new RabbitMQContainer("rabbitmq:3-management");

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", mySQLContainer::getJdbcUrl);
        registry.add("spring.datasource.username", mySQLContainer::getUsername);
        registry.add("spring.datasource.password", mySQLContainer::getPassword);
        registry.add("spring.rabbitmq.host", rabbitMQContainer::getHost);
        registry.add("spring.rabbitmq.port", rabbitMQContainer::getAmqpPort);
    }

    @Autowired
    private SubscriptionService subscriptionService;

    @Autowired
    private PlanRepository planRepository;

    @MockBean
    private BillingService billingService;

    @Test
    void testSubscriptionCreationFlow() {
        Plan plan = new Plan();
        plan.setName("Test Plan");
        plan.setPrice(999);
        plan = planRepository.save(plan);

        when(billingService.createInvoice(any())).thenReturn("ok");

        SubscriptionDto dto = new SubscriptionDto();
        dto.setPlanId(plan.getId());
        dto.setUserId(1L);

        Subscription subscription = subscriptionService.createSubscription(dto);
        assertNotNull(subscription.getId());
        assertNotNull(subscription.getNextRenewalDate());
    }
}
