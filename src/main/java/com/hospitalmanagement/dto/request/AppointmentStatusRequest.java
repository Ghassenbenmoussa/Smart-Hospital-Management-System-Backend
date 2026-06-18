package com.hospitalmanagement.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class AppointmentStatusRequest {

    @NotBlank(message = "Status is required")
    private String status;
}
