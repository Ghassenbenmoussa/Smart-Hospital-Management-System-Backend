package com.hospitalmanagement.controller;

import com.hospitalmanagement.dto.request.MedicalRecordRequest;
import com.hospitalmanagement.dto.response.MedicalRecordHistoryResponse;
import com.hospitalmanagement.dto.response.MedicalRecordResponse;
import com.hospitalmanagement.dto.response.PageResponse;
import com.hospitalmanagement.exception.GlobalExceptionHandler.ApiResponse;
import com.hospitalmanagement.service.MedicalRecordService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/medical-records")
@Tag(name = "Medical Records")
public class MedicalRecordController {

    private final MedicalRecordService medicalRecordService;

    public MedicalRecordController(MedicalRecordService medicalRecordService) {
        this.medicalRecordService = medicalRecordService;
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR')")
    @Operation(summary = "Get all medical records with pagination")
    public ResponseEntity<ApiResponse> getAllMedicalRecords(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id,asc") String sort) {
        String[] sortParams = sort.split(",");
        Sort sorting = Sort.by(
                sortParams[1].equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC,
                sortParams[0]
        );
        Pageable pageable = PageRequest.of(page, size, sorting);
        PageResponse<MedicalRecordResponse> response = medicalRecordService.getAllMedicalRecords(pageable);
        return ApiResponse.success("Medical records retrieved successfully", response);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR')")
    @Operation(summary = "Get medical record by ID")
    public ResponseEntity<ApiResponse> getMedicalRecordById(@PathVariable Long id) {
        MedicalRecordResponse response = medicalRecordService.getMedicalRecordById(id);
        return ApiResponse.success("Medical record retrieved successfully", response);
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR')")
    @Operation(summary = "Create a new medical record")
    public ResponseEntity<ApiResponse> createMedicalRecord(@Valid @RequestBody MedicalRecordRequest request) {
        MedicalRecordResponse response = medicalRecordService.createMedicalRecord(request);
        return ApiResponse.success("Medical record created successfully", response);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR')")
    @Operation(summary = "Update an existing medical record")
    public ResponseEntity<ApiResponse> updateMedicalRecord(@PathVariable Long id,
                                                           @Valid @RequestBody MedicalRecordRequest request) {
        MedicalRecordResponse response = medicalRecordService.updateMedicalRecord(id, request);
        return ApiResponse.success("Medical record updated successfully", response);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR')")
    @Operation(summary = "Delete a medical record")
    public ResponseEntity<ApiResponse> deleteMedicalRecord(@PathVariable Long id) {
        medicalRecordService.deleteMedicalRecord(id);
        return ApiResponse.success("Medical record deleted successfully", null);
    }

    @GetMapping("/{id}/history")
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR')")
    @Operation(summary = "Get version history for a medical record")
    public ResponseEntity<ApiResponse> getMedicalRecordHistory(
            @PathVariable Long id,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "versionDate"));
        PageResponse<MedicalRecordHistoryResponse> response = medicalRecordService.getMedicalRecordHistory(id, pageable);
        return ApiResponse.success("Medical record history retrieved successfully", response);
    }

    @GetMapping("/patient/{patientId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR')")
    @Operation(summary = "Get medical records by patient ID")
    public ResponseEntity<ApiResponse> getMedicalRecordsByPatient(
            @PathVariable Long patientId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id,asc") String sort) {
        String[] sortParams = sort.split(",");
        Sort sorting = Sort.by(
                sortParams[1].equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC,
                sortParams[0]
        );
        Pageable pageable = PageRequest.of(page, size, sorting);
        PageResponse<MedicalRecordResponse> response = medicalRecordService.getMedicalRecordsByPatient(patientId, pageable);
        return ApiResponse.success("Medical records retrieved successfully", response);
    }
}
