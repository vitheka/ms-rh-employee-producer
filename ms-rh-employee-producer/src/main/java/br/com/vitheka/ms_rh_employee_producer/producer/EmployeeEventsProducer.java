package br.com.vitheka.ms_rh_employee_producer.producer;

import br.com.vitheka.ms_rh_employee_producer.requestDto.EmployeeRequest;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@Component
public class EmployeeEventsProducer {

    private static final Logger logger = LoggerFactory.getLogger(EmployeeEventsProducer.class);

    @Value("${spring.kafka.topic}")
    private String topicName;

    private final KafkaTemplate<Integer, String> kafkaTemplate;
    private final ObjectMapper mapper;

    public EmployeeEventsProducer(KafkaTemplate<Integer, String> kafkaTemplate, ObjectMapper mapper) {
        this.kafkaTemplate = kafkaTemplate;
        this.mapper = mapper;
    }

    //1. blocking call - get metadata about the kafka cluster
    //2. Send message happens - Return a CompletableFuture
    public CompletableFuture<SendResult<Integer, String>> sendEmployeeEvent(EmployeeRequest request) throws JsonProcessingException {

        var value = mapper.writeValueAsString(request);

        var completableFuture = kafkaTemplate.send(topicName, value);

        return completableFuture
                .whenComplete(((sendResult, throwable) -> {

                    if (throwable != null) {
                        handleFailure(throwable);
                    }
                    else {
                        handleSuccess(value, sendResult);
                    }
                }));
    }


    //1. blocking call - get metadata about the kafka cluster
    //2. Block and wait until message is sent to the kafka
    public SendResult<Integer, String> approach2(EmployeeRequest request) throws JsonProcessingException, ExecutionException, InterruptedException {

        var value = mapper.writeValueAsString(request);

        var sendResult = kafkaTemplate.send(topicName, value).get();
        handleSuccess(value, sendResult);
        return sendResult;


    }

    private void handleSuccess(String value, SendResult<Integer, String> sendResult) {
        logger.info("Message send Successfully for the value: {} and partition is {} ",
                value, sendResult.getRecordMetadata().partition());
    }

    private void handleFailure(Throwable ex) {

        logger.error("Error sending the message and the exception is {} ", ex.getMessage(), ex);

    }
}
