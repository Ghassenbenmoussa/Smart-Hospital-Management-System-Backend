package com.hospitalmanagement.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class SpecialityRequest {

    @NotBlank(message = "Speciality name is required")
    private String name;
}
