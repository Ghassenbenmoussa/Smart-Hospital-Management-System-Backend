package com.hospitalmanagement.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DashboardResponse {
    private long totalDoctors;
    private long totalPatients;
    private long totalAppointments;
    private long totalMedicalRecords;
    private List<MonthlyAppointmentCount> appointmentsPerMonth;
    private Map<String, Long> patientsByGender;
    private Map<String, Long> appointmentsByStatus;
    private Map<String, Long> doctorsBySpecialty;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MonthlyAppointmentCount {
        private int month;
        private long count;
    }
}
