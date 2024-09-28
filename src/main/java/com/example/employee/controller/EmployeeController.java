package com.example.employee.controller;

import com.example.employee.model.Employee;
import com.example.employee.service.EmployeeService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/employees")
public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;

    // Endpoint to store employee details
    @PostMapping
    public ResponseEntity<Employee> createEmployee(@Valid @RequestBody Employee employee) {
        Employee savedEmployee = employeeService.saveEmployee(employee);
        return new ResponseEntity<>(savedEmployee, HttpStatus.CREATED);
    }

    // Endpoint to return tax deductions for a specific employee
    @GetMapping("/{employeeId}/tax-deductions")
    public ResponseEntity<Employee> getEmployeeTaxDeductions(@PathVariable String employeeId) {
        Employee employee = employeeService.getTaxDeductions(employeeId);
        return new ResponseEntity<>(employee, HttpStatus.OK);
    }
}

