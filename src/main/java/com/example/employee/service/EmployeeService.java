package com.example.employee.service;

import com.example.employee.exception.EmployeeNotFoundException;
import com.example.employee.model.Employee;
import com.example.employee.repository.EmployeeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.Month;
import java.time.temporal.ChronoUnit;

@Service
public class EmployeeService {

    @Autowired
    private EmployeeRepository employeeRepository;

    public Employee saveEmployee(Employee employee) {
        // Additional validations can be added if needed
        return employeeRepository.save(employee);
    }

 // Method to get tax deductions based on employee ID
    public Employee getTaxDeductions(String employeeId) {
        // Retrieve employee from the database
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new EmployeeNotFoundException("Employee not found"));

        // Calculate the yearly salary based on DOJ and monthly salary
        double yearlySalary = calculateYearlySalary(employee.getDoj(), employee.getSalary());
        
        // Calculate tax based on tax slabs
        double taxAmount = calculateTaxAmount(yearlySalary);
        
        // Calculate cess if applicable
        double cessAmount = calculateCess(yearlySalary);

        // Set the calculated values to the employee object
        employee.setYearlySalary(yearlySalary);
        employee.setTaxAmount(taxAmount);
        employee.setCessAmount(cessAmount);

        // Return the employee with calculated tax details
        return employee;
    }

    // Calculate yearly salary based on the date of joining and monthly salary
    private double calculateYearlySalary(LocalDate doj, double monthlySalary) {
        // Financial year start and end dates
        LocalDate financialYearStart = LocalDate.of(doj.getYear(), Month.APRIL, 1);
        LocalDate financialYearEnd = LocalDate.of(doj.getYear() + 1, Month.MARCH, 31);

        // Determine the start date for calculation based on DOJ
        LocalDate startDate = doj.isBefore(financialYearStart) ? financialYearStart : doj;

        // Calculate the number of months worked in the financial year
        long monthsWorked = ChronoUnit.MONTHS.between(startDate.withDayOfMonth(1), financialYearEnd.withDayOfMonth(1)) + 1;

        // Calculate the yearly salary based on months worked
        return monthsWorked * monthlySalary;
    }

    // Calculate tax amount based on yearly salary and tax slabs
    private double calculateTaxAmount(double yearlySalary) {
        double tax = 0.0;

        if (yearlySalary <= 250000) {
            tax = 0;
        } else if (yearlySalary <= 500000) {
            tax = 0.05 * (yearlySalary - 250000);
        } else if (yearlySalary <= 1000000) {
            tax = (0.05 * 250000) + (0.10 * (yearlySalary - 500000));
        } else {
            tax = (0.05 * 250000) + (0.10 * 500000) + (0.20 * (yearlySalary - 1000000));
        }

        return tax;
    }

    // Calculate cess amount if the yearly salary exceeds 2,500,000
    private double calculateCess(double yearlySalary) {
        double cess = 0.0;

        // Apply 2% cess on the amount exceeding 2,500,000
        if (yearlySalary > 2500000) {
            cess = 0.02 * (yearlySalary - 2500000);
        }

        return cess;
    }
}
