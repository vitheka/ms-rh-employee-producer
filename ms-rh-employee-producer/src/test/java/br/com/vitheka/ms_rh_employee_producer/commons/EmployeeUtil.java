package br.com.vitheka.ms_rh_employee_producer.commons;

import br.com.vitheka.ms_rh_employee_producer.enums.EmployeeEventType;
import br.com.vitheka.ms_rh_employee_producer.enums.TypeEmployee;
import br.com.vitheka.ms_rh_employee_producer.requestDto.EmployeeRequest;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class EmployeeUtil {

    public EmployeeRequest newEmployeeRequestCreate() {

        var employee = new EmployeeRequest();

        employee.setEventId(1L);
        employee.setDepartmentId(1L);
        employee.setFirstName("João");
        employee.setLastName("Silva");
        employee.setEmail("joao.silva@example.com");
        employee.setPhoneNumber("123456789");
        employee.setHireDate(LocalDateTime.now());
        employee.setTypeEmployee(TypeEmployee.MEDIUM);
        employee.setEmployeeEventType(EmployeeEventType.NEW);
        employee.setSalary(5000.00);

        return employee;
    }

    public EmployeeRequest newEmployeeRequestInvalid() {

        var employeeInvalid = new EmployeeRequest();

        employeeInvalid.setFirstName("");
        employeeInvalid.setLastName("Silva");
        employeeInvalid.setEmail("");
        employeeInvalid.setPhoneNumber("");
        employeeInvalid.setTypeEmployee(TypeEmployee.MEDIUM);
        employeeInvalid.setHireDate(LocalDateTime.now());
        employeeInvalid.setEmployeeEventType(EmployeeEventType.UPDATE);
        employeeInvalid.setSalary(5000.00);

        return employeeInvalid;
    }
}
