package br.com.vitheka.ms_rh_employee_producer.controller;

import br.com.vitheka.ms_rh_employee_producer.requestDto.EmployeeRequest;
import br.com.vitheka.ms_rh_employee_producer.service.EmployeeService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/employees")
public class EmployeeController {

    private final Logger log = LoggerFactory.getLogger(EmployeeController.class);
    private final ObjectMapper mapper;

    private final EmployeeService employeeService;

    public EmployeeController(ObjectMapper mapper, EmployeeService employeeService) {
        this.mapper = mapper;
        this.employeeService = employeeService;
    }

    @PostMapping
    public ResponseEntity<Void> createEmployeeKafka(@RequestBody @Valid EmployeeRequest request) throws JsonProcessingException {

        log.info("Request receive: {}", mapper.writeValueAsString(request));

        return ResponseEntity.ok(employeeService.createEmployee(request));
    }

}
