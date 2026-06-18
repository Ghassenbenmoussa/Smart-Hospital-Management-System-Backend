package com.hospitalmanagement.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MedicalRecordHistoryResponse {
    private Long id;
    private Long medicalRecordId;
    private String diagnosis;
    private String treatment;
    private String notes;
    private LocalDateTime versionDate;
}
