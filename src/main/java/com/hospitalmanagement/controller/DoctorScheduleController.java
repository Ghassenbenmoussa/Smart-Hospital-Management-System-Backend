package com.hospitalmanagement.controller;

import com.hospitalmanagement.dto.request.DoctorScheduleRequest;
import com.hospitalmanagement.dto.response.DoctorScheduleResponse;
import com.hospitalmanagement.exception.GlobalExceptionHandler.ApiResponse;
import com.hospitalmanagement.service.DoctorScheduleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/doctor-schedules")
@Tag(name = "Doctor Schedules")
public class DoctorScheduleController {

    private final DoctorScheduleService doctorScheduleService;

    public DoctorScheduleController(DoctorScheduleService doctorScheduleService) {
        this.doctorScheduleService = doctorScheduleService;
    }

    @GetMapping("/doctor/{doctorId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR')")
    @Operation(summary = "Get schedules by doctor ID")
    public ResponseEntity<ApiResponse> getSchedulesByDoctor(@PathVariable Long doctorId) {
        List<DoctorScheduleResponse> response = doctorScheduleService.getSchedulesByDoctor(doctorId);
        return ApiResponse.success("Schedules retrieved successfully", response);
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Create a doctor schedule")
    public ResponseEntity<ApiResponse> createSchedule(@Valid @RequestBody DoctorScheduleRequest request) {
        DoctorScheduleResponse response = doctorScheduleService.createSchedule(request);
        return ApiResponse.success("Schedule created successfully", response);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Update a doctor schedule")
    public ResponseEntity<ApiResponse> updateSchedule(@PathVariable Long id,
                                                      @Valid @RequestBody DoctorScheduleRequest request) {
        DoctorScheduleResponse response = doctorScheduleService.updateSchedule(id, request);
        return ApiResponse.success("Schedule updated successfully", response);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Delete a doctor schedule")
    public ResponseEntity<ApiResponse> deleteSchedule(@PathVariable Long id) {
        doctorScheduleService.deleteSchedule(id);
        return ApiResponse.success("Schedule deleted successfully", null);
    }

    @GetMapping("/doctor/{doctorId}/availability")
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR')")
    @Operation(summary = "Check if doctor is available at a given time")
    public ResponseEntity<ApiResponse> checkAvailability(
            @PathVariable Long doctorId,
            @RequestParam String dayOfWeek,
            @RequestParam String time) {
        java.time.DayOfWeek day = java.time.DayOfWeek.valueOf(dayOfWeek.toUpperCase());
        java.time.LocalTime localTime = java.time.LocalTime.parse(time);
        boolean available = doctorScheduleService.isDoctorAvailable(doctorId, day, localTime);
        return ApiResponse.success("Availability checked", java.util.Map.of("available", available));
    }
}
