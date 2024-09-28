package br.com.vitheka.ms_rh_employee_producer.UNIT;

import br.com.vitheka.ms_rh_employee_producer.controller.EmployeeController;
import br.com.vitheka.ms_rh_employee_producer.requestDto.EmployeeRequest;
import br.com.vitheka.ms_rh_employee_producer.service.EmployeeService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.time.LocalDateTime;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(EmployeeController.class)
class EmployeeControllerUnitTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper mapper;

    @MockBean
    EmployeeService employeeService;

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

    }


    @Test
    void createEmployeeKafka() throws Exception {

        var json = mapper.writeValueAsString(employee);

        //when(employeeService.createEmployee(employee)).thenReturn(null);

        mockMvc
                .perform(MockMvcRequestBuilders.post("/employees")
                        .content(json)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

    }
}
