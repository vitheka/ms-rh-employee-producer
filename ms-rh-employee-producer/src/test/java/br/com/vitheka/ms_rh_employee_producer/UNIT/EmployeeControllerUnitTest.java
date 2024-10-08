package br.com.vitheka.ms_rh_employee_producer.UNIT;

import br.com.vitheka.ms_rh_employee_producer.commons.EmployeeUtil;
import br.com.vitheka.ms_rh_employee_producer.controller.EmployeeController;
import br.com.vitheka.ms_rh_employee_producer.enums.EmployeeEventType;
import br.com.vitheka.ms_rh_employee_producer.producer.EmployeeEventsProducer;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.Objects;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(EmployeeController.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class EmployeeControllerUnitTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private EmployeeEventsProducer employeeEventsProducer;

    @Autowired
    private ObjectMapper mapper;

    @InjectMocks
    private EmployeeUtil employeeUtil;


    @Test
    void createEmployeeKafka() throws Exception {

        var employee = employeeUtil.newEmployeeRequestCreate();

        var json = mapper.writeValueAsString(employee);

        mockMvc
                .perform(MockMvcRequestBuilders.post("/employees")
                        .content(json)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

    }

    @Test
    void createEmployeeKafka_ThrowBadRequest_WhenInvalidArguments() throws Exception {

        var employeeInvalid = employeeUtil.newEmployeeRequestInvalid();

        var json = mapper.writeValueAsString(employeeInvalid);

        mockMvc
                .perform(MockMvcRequestBuilders.post("/employees")
                        .content(json)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError());

    }

    @Test
    void createEmployeeKafka_ThrowBadRequest_WhenInvalidtEmployeeEventType() throws Exception {

        var employee = employeeUtil.newEmployeeRequestCreate();

        employee.setEmployeeEventType(EmployeeEventType.UPDATE);
        var json = mapper.writeValueAsString(employee);


        mockMvc
                .perform(MockMvcRequestBuilders.post("/employees")
                        .content(json)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(result -> Assertions
                        .assertEquals("Only NEW event type is supported",
                                Objects.requireNonNull(result.getResponse().getContentAsString())));

    }

    @Test
    void updateEmployeeKafka() throws Exception {

        var id = 1L;

        var employee = employeeUtil.newEmployeeRequestCreate();
        employee.setEmployeeEventType(EmployeeEventType.UPDATE);

        var json = mapper.writeValueAsString(employee);

        mockMvc
                .perform(MockMvcRequestBuilders.put("/employees/" + id)
                        .content(json)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

    }

    @Test
    void updateEmployeeKafka_ThrowBadRequest_WhenInvalidArguments() throws Exception {

        var id = 1L;

        var employeeInvalid = employeeUtil.newEmployeeRequestInvalid();

        var json = mapper.writeValueAsString(employeeInvalid);

        mockMvc
                .perform(MockMvcRequestBuilders.put("/employees/" + id)
                        .content(json)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError());

    }

    @Test
    void updateEmployeeKafka_ThrowBadRequest_WhenInvalidtEmployeeEventType() throws Exception {

        var id = 1L;

        var employee = employeeUtil.newEmployeeRequestCreate();

        var json = mapper.writeValueAsString(employee);

        mockMvc
                .perform(MockMvcRequestBuilders.put("/employees/" + id)
                        .content(json)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(result -> Assertions
                        .assertEquals("Only UPDATE event type is supported",
                                Objects.requireNonNull(result.getResponse().getContentAsString())));

    }
}
