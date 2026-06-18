package com.hospitalmanagement.controller;

import com.hospitalmanagement.dto.request.SpecialityRequest;
import com.hospitalmanagement.dto.response.SpecialityResponse;
import com.hospitalmanagement.exception.GlobalExceptionHandler.ApiResponse;
import com.hospitalmanagement.service.SpecialityService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/specialities")
@Tag(name = "Specialities")
public class SpecialityController {

    private final SpecialityService specialityService;

    public SpecialityController(SpecialityService specialityService) {
        this.specialityService = specialityService;
    }

    @GetMapping
    @Operation(summary = "Get all specialities")
    public ResponseEntity<ApiResponse> getAll() {
        List<SpecialityResponse> response = specialityService.getAll();
        return ApiResponse.success("Specialities retrieved successfully", response);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get speciality by ID")
    public ResponseEntity<ApiResponse> getById(@PathVariable Long id) {
        SpecialityResponse response = specialityService.getById(id);
        return ApiResponse.success("Speciality retrieved successfully", response);
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Create a new speciality")
    public ResponseEntity<ApiResponse> create(@Valid @RequestBody SpecialityRequest request) {
        SpecialityResponse response = specialityService.create(request);
        return ApiResponse.success("Speciality created successfully", response);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Update a speciality")
    public ResponseEntity<ApiResponse> update(@PathVariable Long id,
                                              @Valid @RequestBody SpecialityRequest request) {
        SpecialityResponse response = specialityService.update(id, request);
        return ApiResponse.success("Speciality updated successfully", response);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Delete a speciality")
    public ResponseEntity<ApiResponse> delete(@PathVariable Long id) {
        specialityService.delete(id);
        return ApiResponse.success("Speciality deleted successfully", null);
    }
}
