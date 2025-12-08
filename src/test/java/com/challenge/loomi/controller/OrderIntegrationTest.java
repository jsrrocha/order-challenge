package com.challenge.loomi.controller;

import com.challenge.loomi.api.dto.request.CreateOrderRequest;
import com.challenge.loomi.api.dto.request.OrderItemRequest;
import com.challenge.loomi.domain.entity.Order;
import com.challenge.loomi.domain.entity.Product;
import com.challenge.loomi.domain.enums.OrderStatus;
import com.challenge.loomi.domain.enums.OrderType;
import com.challenge.loomi.repository.OrderRepository;
import com.challenge.loomi.repository.ProductRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.clients.admin.NewTopic;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(properties = {
        "spring.datasource.url=jdbc:postgresql://127.0.0.1:5432/order",
        "spring.datasource.username=postgres",
        "spring.datasource.password=post",
        "spring.datasource.driver-class-name=org.postgresql.Driver",

        "spring.kafka.bootstrap-servers=127.0.0.1:19092",

        "spring.kafka.producer.key-serializer=org.apache.kafka.common.serialization.StringSerializer",
        "spring.kafka.producer.value-serializer=org.springframework.kafka.support.serializer.JsonSerializer",

        "spring.kafka.consumer.key-deserializer=org.apache.kafka.common.serialization.StringDeserializer",
        "spring.kafka.consumer.value-deserializer=org.springframework.kafka.support.serializer.JsonDeserializer",
        "spring.kafka.consumer.properties.spring.json.trusted.packages=*",
        "spring.kafka.consumer.group-id=test-group-integration",

        "spring.jpa.hibernate.ddl-auto=create-drop",
        "spring.jpa.defer-datasource-initialization=true",
        "spring.sql.init.mode=always"
})
@AutoConfigureMockMvc
class OrderIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ObjectMapper objectMapper;

    /**
     * Configuração dos Tópicos para o Teste
     * Isso evita TimeoutException se o Kafka tentar conectar num tópico que não existe.
     */
    @TestConfiguration
    static class KafkaTestConfig {
        @Bean
        public NewTopic orderEventsTopic() {
            return new NewTopic("order-events", 1, (short) 1);
        }

        @Bean
        public NewTopic stockAlertsTopic() {
            return new NewTopic("stock-alerts", 1, (short) 1);
        }

        // --- IMPORTANTE: Adicionado o tópico de falhas ---
        @Bean
        public NewTopic orderFailuresTopic() {
            return new NewTopic("order-failures", 1, (short) 1);
        }
    }

    @BeforeEach
    void setup() {
        orderRepository.deleteAll();
        productRepository.deleteAll();
    }

    @Test
    @DisplayName("Should create order and process it asynchronously (E2E Flow)")
    void shouldCreateOrderSuccessfully() throws Exception {
        // 1. ARRANGE: Criar Produto
        Product product = Product.builder()
                .id("TEST-INTEGRATION")
                .name("Test Product Integration")
                .type(OrderType.PHYSICAL)
                .price(new BigDecimal("100.00"))
                .active(true)
                .stockQuantity(50)
                .build();
        productRepository.save(product);

        // 2. ACT: Chamar API (POST)
        var itemReq = new OrderItemRequest("TEST-INTEGRATION", 2, Map.of("color", "blue"));
        var request = new CreateOrderRequest("customer-integration-test", List.of(itemReq));

        MvcResult result = mockMvc.perform(post("/api/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.orderId", notNullValue()))
                .andExpect(jsonPath("$.status", is("PENDING"))) // Status inicial síncrono
                .andExpect(jsonPath("$.totalAmount", is(200.0)))
                .andReturn();

        // Extrair o ID do pedido criado da resposta
        String responseJson = result.getResponse().getContentAsString();
        String orderIdStr = objectMapper.readTree(responseJson).get("orderId").asText();
        UUID orderId = UUID.fromString(orderIdStr);

        // 3. ASSERT ASYNC: Esperar o Kafka Consumer processar (PENDING -> PROCESSED)
        // O Awaitility vai tentar essa checagem várias vezes por até 5 segundos
        await().atMost(Duration.ofSeconds(10))
                .pollInterval(Duration.ofMillis(500))
                .untilAsserted(() -> {
                    Order updatedOrder = orderRepository.findById(orderId).orElseThrow();

                    // Verifica se o status mudou para PROCESSED (significa que o Kafka funcionou)
                    assertEquals(OrderStatus.PROCESSED, updatedOrder.getStatus());

                    // Verifica se o estoque foi baixado (50 - 2 = 48)
                    Product updatedProduct = productRepository.findById("TEST-INTEGRATION").orElseThrow();
                    assertEquals(48, updatedProduct.getStockQuantity());
                });
    }
}