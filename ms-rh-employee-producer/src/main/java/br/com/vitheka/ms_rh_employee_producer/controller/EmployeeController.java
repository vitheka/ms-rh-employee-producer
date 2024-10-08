package br.com.vitheka.ms_rh_employee_producer.controller;

import br.com.vitheka.ms_rh_employee_producer.enums.EmployeeEventType;
import br.com.vitheka.ms_rh_employee_producer.producer.EmployeeEventsProducer;
import br.com.vitheka.ms_rh_employee_producer.requestDto.EmployeeRequest;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/employees")
public class EmployeeController {

    private final Logger log = LoggerFactory.getLogger(EmployeeController.class);
    private final ObjectMapper mapper;

    private final EmployeeEventsProducer producer;

    public EmployeeController(ObjectMapper mapper, EmployeeEventsProducer producer) {
        this.mapper = mapper;
        this.producer = producer;
    }

    @PostMapping
    public ResponseEntity<?> createEmployeeKafka(@RequestBody @Valid EmployeeRequest request) throws JsonProcessingException {

        log.info("Request receive: {}", mapper.writeValueAsString(request));

        if (request.getEmployeeEventType() != EmployeeEventType.NEW) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Only NEW event type is supported");
        }
        return ResponseEntity.ok(producer.sendEmployeeEvent(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateEmployeeKafka(@PathVariable Long id, @RequestBody @Valid EmployeeRequest request) throws JsonProcessingException {

        log.info("Id: {}, Request UPDATE receive: {}", id, mapper.writeValueAsString(request));

        if (request.getEmployeeEventType() != EmployeeEventType.UPDATE) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Only UPDATE event type is supported");
        }
        return ResponseEntity.ok(producer.sendEmployeeEvent(request));
    }

}
