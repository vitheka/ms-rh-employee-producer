package br.com.vitheka.ms_rh_employee_producer;

import br.com.vitheka.ms_rh_employee_producer.requestDto.EmployeeRequest;
import br.com.vitheka.ms_rh_employee_producer.service.EmployeeService;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.common.serialization.IntegerDeserializer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.kafka.test.utils.KafkaTestUtils;
import org.springframework.test.context.TestPropertySource;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@EmbeddedKafka(topics = {"employee-events"}, partitions = 3)
@TestPropertySource(properties = {
        "spring.kafka.producer.bootstrap-servers=${spring.embedded.kafka.brokers}",
        "spring.kafka.admin.properties.bootstrap.servers=${spring.embedded.kafka.brokers}"
})
class EmployeeControllerTest {

    @Autowired
    TestRestTemplate restTemplate;

    @Autowired
    EmployeeService employeeService;

    @Autowired
    EmbeddedKafkaBroker embeddedKafkaBroker;

    private Consumer<Integer, String> consumer;

    private EmployeeRequest employee;


    @BeforeEach
    void init() {

        employee = new EmployeeRequest();

        employee.setFirstName("João");
        employee.setLastName("Silva");
        employee.setEmail("joao.silva@example.com");
        employee.setPhoneNumber("123456789");
        employee.setHireDate(LocalDateTime.now());
        employee.setSalary(5000.00);

        Map<String, Object> configs = new HashMap<>(KafkaTestUtils.consumerProps("group1", "true", embeddedKafkaBroker));
        configs.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "latest");
        consumer = new DefaultKafkaConsumerFactory<>(configs, new IntegerDeserializer(), new StringDeserializer()).createConsumer();
        embeddedKafkaBroker.consumeFromAllEmbeddedTopics(consumer);

    }

    @AfterEach
    void tearDown() {
        consumer.close();
    }

    @Test
    void createEmployeeKafka()  {

        var headers = new HttpHeaders();
        headers.set("content-type", MediaType.APPLICATION_JSON.toString());

        var httpEntity = new HttpEntity<>(employee, headers);

        var response = restTemplate
                .exchange("/employees", HttpMethod.POST,
                        httpEntity, EmployeeRequest.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());

        ConsumerRecords<Integer, String> consumerRecords = KafkaTestUtils.getRecords(consumer);

        assert consumerRecords.count() == 1;

    }
}