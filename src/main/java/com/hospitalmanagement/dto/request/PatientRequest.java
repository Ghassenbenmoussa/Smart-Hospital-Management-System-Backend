package com.hospitalmanagement.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class PatientRequest {

    @NotBlank(message = "First name is required")
    private String firstName;

    @NotBlank(message = "Last name is required")
    private String lastName;

    private String birthDate;

    private String gender;

    private String phone;

    private String address;

    private String emergencyContact;
}
