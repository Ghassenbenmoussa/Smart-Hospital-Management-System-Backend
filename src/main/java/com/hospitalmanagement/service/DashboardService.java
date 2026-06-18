package com.hospitalmanagement.service;

import com.hospitalmanagement.dto.response.DashboardResponse;
import com.hospitalmanagement.dto.response.DashboardResponse.MonthlyAppointmentCount;
import com.hospitalmanagement.entity.Appointment;
import com.hospitalmanagement.enums.AppointmentStatus;
import com.hospitalmanagement.enums.Gender;
import com.hospitalmanagement.repository.AppointmentRepository;
import com.hospitalmanagement.repository.DoctorRepository;
import com.hospitalmanagement.repository.MedicalRecordRepository;
import com.hospitalmanagement.repository.PatientRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
@Transactional(readOnly = true)
public class DashboardService {

    private final DoctorRepository doctorRepository;
    private final PatientRepository patientRepository;
    private final AppointmentRepository appointmentRepository;
    private final MedicalRecordRepository medicalRecordRepository;

    public DashboardService(DoctorRepository doctorRepository,
                            PatientRepository patientRepository,
                            AppointmentRepository appointmentRepository,
                            MedicalRecordRepository medicalRecordRepository) {
        this.doctorRepository = doctorRepository;
        this.patientRepository = patientRepository;
        this.appointmentRepository = appointmentRepository;
        this.medicalRecordRepository = medicalRecordRepository;
    }

    public DashboardResponse getDashboardData() {
        long totalDoctors = doctorRepository.count();
        long totalPatients = patientRepository.count();
        long totalAppointments = appointmentRepository.count();
        long totalMedicalRecords = medicalRecordRepository.count();

        List<MonthlyAppointmentCount> appointmentsPerMonth = getAppointmentsPerMonth();
        Map<String, Long> patientsByGender = getPatientsByGender();
        Map<String, Long> appointmentsByStatus = getAppointmentsByStatus();
        Map<String, Long> doctorsBySpecialty = getDoctorsBySpecialty();

        return DashboardResponse.builder()
                .totalDoctors(totalDoctors)
                .totalPatients(totalPatients)
                .totalAppointments(totalAppointments)
                .totalMedicalRecords(totalMedicalRecords)
                .appointmentsPerMonth(appointmentsPerMonth)
                .patientsByGender(patientsByGender)
                .appointmentsByStatus(appointmentsByStatus)
                .doctorsBySpecialty(doctorsBySpecialty)
                .build();
    }

    private List<MonthlyAppointmentCount> getAppointmentsPerMonth() {
        int currentYear = LocalDateTime.now().getYear();
        LocalDateTime startOfYear = LocalDateTime.of(currentYear, 1, 1, 0, 0);
        LocalDateTime startOfNextYear = startOfYear.plusYears(1);

        List<Appointment> yearAppointments = appointmentRepository
                .findByAppointmentDateBetween(startOfYear, startOfNextYear);

        Map<Integer, Long> countsByMonth = yearAppointments.stream()
                .collect(Collectors.groupingBy(
                        a -> a.getAppointmentDate().getMonthValue(),
                        Collectors.counting()
                ));

        return IntStream.rangeClosed(1, 12)
                .mapToObj(month -> MonthlyAppointmentCount.builder()
                        .month(month)
                        .count(countsByMonth.getOrDefault(month, 0L))
                        .build())
                .toList();
    }

    private Map<String, Long> getPatientsByGender() {
        Map<String, Long> result = new LinkedHashMap<>();
        result.put("MALE", patientRepository.countByGender(Gender.MALE));
        result.put("FEMALE", patientRepository.countByGender(Gender.FEMALE));
        return result;
    }

    private Map<String, Long> getAppointmentsByStatus() {
        return Arrays.stream(AppointmentStatus.values())
                .collect(Collectors.toMap(
                        Enum::name,
                        s -> appointmentRepository.countByStatus(s)
                ));
    }

    private Map<String, Long> getDoctorsBySpecialty() {
        List<Object[]> results = doctorRepository.countBySpeciality();
        return results.stream()
                .collect(Collectors.toMap(
                        row -> (String) row[0],
                        row -> (Long) row[1]
                ));
    }
}
