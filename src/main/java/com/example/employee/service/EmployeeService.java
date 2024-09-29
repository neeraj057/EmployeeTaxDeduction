package com.example.employee.service;

import com.example.employee.exception.EmployeeNotFoundException;
import com.example.employee.model.Employee;
import com.example.employee.model.TaxDetails;
import com.example.employee.repository.EmployeeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

@Service
public class EmployeeService {

    @Autowired
    private EmployeeRepository employeeRepository;

    public void saveEmployee(Employee employee) {
        if (employeeRepository.existsById(employee.getEmployeeId())) {
            throw new IllegalArgumentException("Employee ID already exists");
        }
        employeeRepository.save(employee);
    }

    public TaxDetails calculateTaxDeductions(String employeeId) {
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new EmployeeNotFoundException("Employee not found"));

        double yearlySalary = calculateYearlySalary(employee);
        double taxAmount = calculateTax(yearlySalary);
        double cessAmount = calculateCess(yearlySalary);

        return new TaxDetails(employeeId, employee.getFirstName(), employee.getLastName(), yearlySalary, taxAmount, cessAmount);
    }

    private double calculateYearlySalary(Employee employee) {
        LocalDate doj = employee.getDoj();
        LocalDate today = LocalDate.now();
        long monthsWorked = ChronoUnit.MONTHS.between(doj.withDayOfMonth(1), today.withDayOfMonth(1)) + 1; // Include the month of DOJ
        return monthsWorked * employee.getSalary();
    }

    private double calculateTax(double yearlySalary) {
        double tax = 0;
        if (yearlySalary <= 250000) return 0; // No tax
        if (yearlySalary <= 500000) {
            tax = (yearlySalary - 250000) * 0.05; // 5% on (yearlySalary - 250000)
        } else if (yearlySalary <= 1000000) {
            tax = 250000 * 0.05 + (yearlySalary - 500000) * 0.10; // 5% on 250000 and 10% on the rest
        } else {
            tax = 250000 * 0.05 + 500000 * 0.10 + (yearlySalary - 1000000) * 0.20; // 5% + 10% + 20%
        }
        return tax;
    }

    private double calculateCess(double yearlySalary) {
        return yearlySalary > 2500000 ? (yearlySalary - 2500000) * 0.02 : 0; // 2% cess on income above 2,500,000
    }
}
