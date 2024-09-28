package br.com.vitheka.ms_rh_employee_producer.service;

import br.com.vitheka.ms_rh_employee_producer.producer.EmployeeEventsProducer;
import br.com.vitheka.ms_rh_employee_producer.requestDto.EmployeeRequest;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.stereotype.Service;

@Service
public class EmployeeService {

    private final EmployeeEventsProducer producerKafkaMessage;

    public EmployeeService(EmployeeEventsProducer producerKafkaMessage) {
        this.producerKafkaMessage = producerKafkaMessage;
    }

    public Void createEmployee(EmployeeRequest request) throws JsonProcessingException {

        producerKafkaMessage.sendEmployeeEvent(request);

        return null;
    }
}
