package com.example.employee.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.employee.model.Employee;
import com.example.employee.model.TaxDetails;
import com.example.employee.repository.EmployeeRepository;

@ExtendWith(MockitoExtension.class)
public class EmployeeServiceTest {

    @InjectMocks
    private EmployeeService employeeService;

    @Mock
    private EmployeeRepository employeeRepository;

    // Test for no tax below the threshold
    @Test
    public void testTaxCalculation_NoTax_BelowThreshold() {
        Employee employee = createEmployee("E001", LocalDate.of(2023, 4, 1), 20000); // Yearly salary = 240,000
        when(employeeRepository.findById("E001")).thenReturn(Optional.of(employee));
        
        TaxDetails taxDetails = employeeService.calculateTaxDeductions("E001");
        
        assertEquals(360000, taxDetails.getYearlySalary());
        assertEquals(5500, taxDetails.getTaxAmount());
        assertEquals(0, taxDetails.getCessAmount());
    }

    // Test for 5% tax between ₹250,000 and ₹500,000
    @Test
    public void testTaxCalculation_5PercentTax_Between250kAnd500k() {
        Employee employee = createEmployee("E002", LocalDate.of(2023, 4, 1), 30000); // Yearly salary = 540,000
        when(employeeRepository.findById("E002")).thenReturn(Optional.of(employee));
        
        TaxDetails taxDetails = employeeService.calculateTaxDeductions("E002");

        assertEquals(540000, taxDetails.getYearlySalary());
        assertEquals(16500, taxDetails.getTaxAmount(), 0.001); // 16,500 tax calculated
        assertEquals(0.0, taxDetails.getCessAmount());
    }

    // Test for 10% tax between ₹500,000 and ₹1,000,000
    @Test
    public void testTaxCalculation_10PercentTax_Between500kAnd1Million() {
        Employee employee = createEmployee("E003", LocalDate.of(2023, 4, 1), 70000); // Yearly salary = 840,000
        when(employeeRepository.findById("E003")).thenReturn(Optional.of(employee));
        
        TaxDetails taxDetails = employeeService.calculateTaxDeductions("E003");

        assertEquals(1260000.0, taxDetails.getYearlySalary());
        assertEquals(114500.0, taxDetails.getTaxAmount(), 0.001);
        assertEquals(0.0, taxDetails.getCessAmount());
    }

    // Test for 20% tax above ₹1,000,000
    @Test
    public void testTaxCalculation_20PercentTax_Above1Million() {
        Employee employee = createEmployee("E004", LocalDate.of(2023, 4, 1), 150000); // Yearly salary =  ₹2,700,000
        when(employeeRepository.findById("E004")).thenReturn(Optional.of(employee));
        
        TaxDetails taxDetails = employeeService.calculateTaxDeductions("E004");

        assertEquals(2700000, taxDetails.getYearlySalary());
        assertEquals(402500, taxDetails.getTaxAmount(), 0.001);
        assertEquals(4000, taxDetails.getCessAmount());
    }

    // Test for additional 2% cess
    @Test
    public void testTaxCalculation_WithCess() {
        Employee employee = createEmployee("E005", LocalDate.of(2023, 4, 1), 300000); // Yearly salary = 3,600,000
        when(employeeRepository.findById("E005")).thenReturn(Optional.of(employee));
        
        TaxDetails taxDetails = employeeService.calculateTaxDeductions("E005");

        assertEquals(5400000, taxDetails.getYearlySalary());
        assertEquals(942500, taxDetails.getTaxAmount(), 0.001); // 5% on 250,000 + 10% on 500,000 + 20% on 1,800,000
        assertEquals(58000, taxDetails.getCessAmount(), 0.001); // 2% cess on 1,100,000 (exceeding 2,500,000)
    }

    // Test for prorated salary based on date of joining
    @Test
    public void testProratedSalaryBasedOnDOJ() {
        Employee employee = createEmployee("E006", LocalDate.of(2023, 10, 1), 50000); // Joined mid-year (October)
        when(employeeRepository.findById("E006")).thenReturn(Optional.of(employee));
        
        TaxDetails taxDetails = employeeService.calculateTaxDeductions("E006");

        assertEquals(600000, taxDetails.getYearlySalary(), 0.001); // Salary prorated for 6 months
        assertEquals(22500, taxDetails.getTaxAmount());
        assertEquals(0, taxDetails.getCessAmount());
    }

    // Test for an employee earning just below the cess threshold
    @Test
    public void testTaxCalculation_CessThreshold() {
        Employee employee = createEmployee("E007", LocalDate.of(2023, 4, 1), 300000); 
        when(employeeRepository.findById("E007")).thenReturn(Optional.of(employee));
        
        TaxDetails taxDetails = employeeService.calculateTaxDeductions("E007");

        assertEquals(5400000, taxDetails.getYearlySalary());
        assertEquals(942500, taxDetails.getTaxAmount(), 0.001); 
        assertEquals(58000, taxDetails.getCessAmount(), 0.001); 
    }

    // Test for a low salary employee that doesn't reach the threshold
    @Test
    public void testTaxCalculation_LowSalary_NoTax() {
        Employee employee = createEmployee("E008", LocalDate.of(2023, 4, 1), 8000); // Yearly salary = 96,000
        when(employeeRepository.findById("E008")).thenReturn(Optional.of(employee));
        
        TaxDetails taxDetails = employeeService.calculateTaxDeductions("E008");

        assertEquals(144000, taxDetails.getYearlySalary());
        assertEquals(0, taxDetails.getTaxAmount());
        assertEquals(0, taxDetails.getCessAmount());
    }

    private Employee createEmployee(String id, LocalDate doj, double monthlySalary) {
        return new Employee(id, "John", "Doe", "john.doe@example.com", 
                            Arrays.asList("1234567890"), doj, monthlySalary);
    }
}
