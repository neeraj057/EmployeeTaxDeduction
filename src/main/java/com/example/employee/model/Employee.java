package com.example.employee.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;
@Entity
@Data
@AllArgsConstructor
public class Employee {
    @Id
    private String employeeId;

    @NotBlank(message = "First name is mandatory")
    private String firstName;

    @NotBlank(message = "Last name is mandatory")
    private String lastName;

    @Email(message = "Email should be valid")
    @NotBlank(message = "Email is mandatory")
    private String email;

    @ElementCollection
    @Size(min = 1, message = "At least one phone number is required")
    private List<@Pattern(regexp = "^[0-9]{10}$", message = "Phone number must be valid") String> phoneNumbers;

    @NotNull(message = "Date of joining is mandatory")
    @PastOrPresent(message = "Date of joining cannot be in the future")
    private LocalDate doj;

    @Min(value = 1, message = "Salary must be greater than 0")
    private double salary;

    // Getters and Setters
}
