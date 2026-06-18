package com.hospitalmanagement.service;

import com.hospitalmanagement.dto.request.AppointmentRequest;
import com.hospitalmanagement.dto.response.AppointmentResponse;
import com.hospitalmanagement.dto.response.PageResponse;
import com.hospitalmanagement.entity.Appointment;
import com.hospitalmanagement.entity.Doctor;
import com.hospitalmanagement.entity.Patient;
import com.hospitalmanagement.enums.AppointmentStatus;
import com.hospitalmanagement.exception.BadRequestException;
import com.hospitalmanagement.exception.ResourceNotFoundException;
import com.hospitalmanagement.mapper.AppointmentMapper;
import com.hospitalmanagement.repository.AppointmentRepository;
import com.hospitalmanagement.repository.DoctorRepository;
import com.hospitalmanagement.repository.PatientRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.hospitalmanagement.util.DateUtils.parseIsoDateTime;

import static com.hospitalmanagement.enums.AppointmentStatus.*;

@Service
@Transactional
public class AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final DoctorRepository doctorRepository;
    private final PatientRepository patientRepository;
    private final DoctorScheduleService doctorScheduleService;
    private final AppointmentMapper appointmentMapper;

    private static final Map<AppointmentStatus, Set<AppointmentStatus>> VALID_TRANSITIONS = Map.of(
            SCHEDULED, Set.of(COMPLETED, CANCELLED),
            COMPLETED, Set.of(),
            CANCELLED, Set.of()
    );

    public AppointmentService(AppointmentRepository appointmentRepository,
                              DoctorRepository doctorRepository,
                              PatientRepository patientRepository,
                              DoctorScheduleService doctorScheduleService,
                              AppointmentMapper appointmentMapper) {
        this.appointmentRepository = appointmentRepository;
        this.doctorRepository = doctorRepository;
        this.patientRepository = patientRepository;
        this.doctorScheduleService = doctorScheduleService;
        this.appointmentMapper = appointmentMapper;
    }

    @Transactional(readOnly = true)
    public PageResponse<AppointmentResponse> getAllAppointments(Pageable pageable) {
        Page<Appointment> page = appointmentRepository.findAll(pageable);
        return buildPageResponse(page);
    }

    @Transactional(readOnly = true)
    public AppointmentResponse getAppointmentById(Long id) {
        Appointment appointment = findAppointmentById(id);
        return appointmentMapper.toResponse(appointment);
    }

    public AppointmentResponse createAppointment(AppointmentRequest request) {
        Doctor doctor = doctorRepository.findById(request.getDoctorId())
                .orElseThrow(() -> new ResourceNotFoundException("Doctor", "id", request.getDoctorId()));
        Patient patient = patientRepository.findById(request.getPatientId())
                .orElseThrow(() -> new ResourceNotFoundException("Patient", "id", request.getPatientId()));

        LocalDateTime appointmentDate = parseIsoDateTime(request.getAppointmentDate());
        checkForConflict(doctor.getId(), appointmentDate, null);

        Appointment appointment = appointmentMapper.toEntity(request);
        appointment.setDoctor(doctor);
        appointment.setPatient(patient);

        appointment = appointmentRepository.save(appointment);
        return appointmentMapper.toResponse(appointment);
    }

    public AppointmentResponse updateAppointment(Long id, AppointmentRequest request) {
        Appointment appointment = findAppointmentById(id);
        Doctor doctor = doctorRepository.findById(request.getDoctorId())
                .orElseThrow(() -> new ResourceNotFoundException("Doctor", "id", request.getDoctorId()));
        Patient patient = patientRepository.findById(request.getPatientId())
                .orElseThrow(() -> new ResourceNotFoundException("Patient", "id", request.getPatientId()));

        LocalDateTime appointmentDate = parseIsoDateTime(request.getAppointmentDate());
        checkForConflict(doctor.getId(), appointmentDate, id);

        Appointment updated = appointmentMapper.toEntity(request);
        updated.setId(id);
        updated.setDoctor(doctor);
        updated.setPatient(patient);
        updated.setStatus(appointment.getStatus());
        updated.setCreatedAt(appointment.getCreatedAt());

        updated = appointmentRepository.save(updated);
        return appointmentMapper.toResponse(updated);
    }

    public AppointmentResponse updateAppointmentStatus(Long id, String newStatus) {
        Appointment appointment = findAppointmentById(id);
        AppointmentStatus currentStatus = appointment.getStatus();
        AppointmentStatus targetStatus;

        try {
            targetStatus = AppointmentStatus.valueOf(newStatus.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new BadRequestException("Invalid status: " + newStatus);
        }

        if (currentStatus == targetStatus) {
            throw new BadRequestException("Appointment is already " + currentStatus);
        }

        Set<AppointmentStatus> allowed = VALID_TRANSITIONS.get(currentStatus);
        if (allowed == null || !allowed.contains(targetStatus)) {
            throw new BadRequestException(
                    "Cannot transition from " + currentStatus + " to " + targetStatus +
                    ". Valid transitions from " + currentStatus + ": " +
                    (allowed == null || allowed.isEmpty() ? "none" : allowed)
            );
        }

        appointment.setStatus(targetStatus);
        appointment = appointmentRepository.save(appointment);
        return appointmentMapper.toResponse(appointment);
    }

    public void deleteAppointment(Long id) {
        Appointment appointment = findAppointmentById(id);
        appointmentRepository.delete(appointment);
    }

    @Transactional(readOnly = true)
    public PageResponse<AppointmentResponse> getAppointmentsByDoctor(Long doctorId, Pageable pageable) {
        if (!doctorRepository.existsById(doctorId)) {
            throw new ResourceNotFoundException("Doctor", "id", doctorId);
        }
        Page<Appointment> page = appointmentRepository.findByDoctorId(doctorId, pageable);
        return buildPageResponse(page);
    }

    @Transactional(readOnly = true)
    public PageResponse<AppointmentResponse> getAppointmentsByPatient(Long patientId, Pageable pageable) {
        if (!patientRepository.existsById(patientId)) {
            throw new ResourceNotFoundException("Patient", "id", patientId);
        }
        Page<Appointment> page = appointmentRepository.findByPatientId(patientId, pageable);
        return buildPageResponse(page);
    }

    @Transactional(readOnly = true)
    public long getTotalCount() {
        return appointmentRepository.count();
    }

    private void checkForConflict(Long doctorId, LocalDateTime appointmentDate, Long excludeAppointmentId) {
        DayOfWeek dayOfWeek = appointmentDate.getDayOfWeek();
        LocalTime time = appointmentDate.toLocalTime();

        if (!doctorScheduleService.isDoctorAvailable(doctorId, dayOfWeek, time)) {
            throw new BadRequestException(
                    "Doctor is not available at " + time + " on " + dayOfWeek
            );
        }

        LocalDateTime start = appointmentDate.minusMinutes(30);
        LocalDateTime end = appointmentDate.plusMinutes(30);
        List<Appointment> conflicts = appointmentRepository
                .findByDoctorIdAndAppointmentDateBetween(doctorId, start, end);

        if (excludeAppointmentId != null) {
            conflicts = conflicts.stream()
                    .filter(a -> !a.getId().equals(excludeAppointmentId))
                    .filter(a -> a.getStatus() == SCHEDULED)
                    .toList();
        } else {
            conflicts = conflicts.stream()
                    .filter(a -> a.getStatus() == SCHEDULED)
                    .toList();
        }

        if (!conflicts.isEmpty()) {
            throw new BadRequestException(
                    "Doctor already has a scheduled appointment within 30 minutes of the requested time"
            );
        }
    }

    private Appointment findAppointmentById(Long id) {
        return appointmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Appointment", "id", id));
    }

    private PageResponse<AppointmentResponse> buildPageResponse(Page<Appointment> page) {
        return PageResponse.<AppointmentResponse>builder()
                .content(page.getContent().stream().map(appointmentMapper::toResponse).toList())
                .page(page.getNumber())
                .size(page.getSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .last(page.isLast())
                .build();
    }
}
