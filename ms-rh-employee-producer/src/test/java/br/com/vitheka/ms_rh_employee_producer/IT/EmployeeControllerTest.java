package br.com.vitheka.ms_rh_employee_producer.IT;

import br.com.vitheka.ms_rh_employee_producer.commons.EmployeeUtil;
import br.com.vitheka.ms_rh_employee_producer.enums.EmployeeEventType;
import br.com.vitheka.ms_rh_employee_producer.requestDto.EmployeeRequest;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.common.serialization.IntegerDeserializer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.kafka.test.utils.KafkaTestUtils;
import org.springframework.test.context.TestPropertySource;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@EmbeddedKafka(topics = {"employee-events"}, partitions = 3)
@TestPropertySource(properties = {
        "spring.kafka.producer.bootstrap-servers=${spring.embedded.kafka.brokers}",
        "spring.kafka.admin.properties.bootstrap.servers=${spring.embedded.kafka.brokers}"
})
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class EmployeeControllerTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private EmbeddedKafkaBroker embeddedKafkaBroker;

    @Autowired
    private EmployeeUtil employeeUtil;

    private Consumer<Integer, String> consumer;

    private EmployeeRequest employee;


    @BeforeEach
    void init() {

        employee = employeeUtil.newEmployeeRequestCreate();

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
    @Order(1)
    @DisplayName("cria evento employee e envia quando sucesso")
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

    @Test
    @Order(2)
    @DisplayName("atualiza evento employee e envia quando sucesso")
    void updateEmployeeKafka()  {

        var id = 1L;

        employee.setEmployeeEventType(EmployeeEventType.UPDATE);

        var headers = new HttpHeaders();
        headers.set("content-type", MediaType.APPLICATION_JSON.toString());

        var httpEntity = new HttpEntity<>(employee, headers);

        var response = restTemplate
                .exchange("/employees/" + id, HttpMethod.PUT,
                        httpEntity, EmployeeRequest.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());

        ConsumerRecords<Integer, String> consumerRecords = KafkaTestUtils.getRecords(consumer);

        assert consumerRecords.count() == 2;

    }
}