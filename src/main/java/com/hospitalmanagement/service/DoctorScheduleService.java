package com.hospitalmanagement.service;

import com.hospitalmanagement.dto.request.DoctorScheduleRequest;
import com.hospitalmanagement.dto.response.DoctorScheduleResponse;
import com.hospitalmanagement.entity.Doctor;
import com.hospitalmanagement.entity.DoctorSchedule;
import com.hospitalmanagement.exception.BadRequestException;
import com.hospitalmanagement.exception.ResourceNotFoundException;
import com.hospitalmanagement.repository.DoctorRepository;
import com.hospitalmanagement.repository.DoctorScheduleRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class DoctorScheduleService {

    private final DoctorScheduleRepository doctorScheduleRepository;
    private final DoctorRepository doctorRepository;

    public DoctorScheduleService(DoctorScheduleRepository doctorScheduleRepository,
                                 DoctorRepository doctorRepository) {
        this.doctorScheduleRepository = doctorScheduleRepository;
        this.doctorRepository = doctorRepository;
    }

    @Transactional(readOnly = true)
    public List<DoctorScheduleResponse> getSchedulesByDoctor(Long doctorId) {
        if (!doctorRepository.existsById(doctorId)) {
            throw new ResourceNotFoundException("Doctor", "id", doctorId);
        }
        return doctorScheduleRepository.findByDoctorId(doctorId).stream()
                .map(this::toResponse)
                .toList();
    }

    public DoctorScheduleResponse createSchedule(DoctorScheduleRequest request) {
        Doctor doctor = doctorRepository.findById(request.getDoctorId())
                .orElseThrow(() -> new ResourceNotFoundException("Doctor", "id", request.getDoctorId()));

        DayOfWeek dayOfWeek = parseDayOfWeek(request.getDayOfWeek());
        LocalTime startTime = LocalTime.parse(request.getStartTime());
        LocalTime endTime = LocalTime.parse(request.getEndTime());

        if (!endTime.isAfter(startTime)) {
            throw new BadRequestException("End time must be after start time");
        }

        if (doctorScheduleRepository.existsByDoctorIdAndDayOfWeek(doctor.getId(), dayOfWeek)) {
            throw new BadRequestException("Schedule already exists for " + dayOfWeek);
        }

        DoctorSchedule schedule = DoctorSchedule.builder()
                .doctor(doctor)
                .dayOfWeek(dayOfWeek)
                .startTime(startTime)
                .endTime(endTime)
                .isAvailable(request.isAvailable())
                .build();

        schedule = doctorScheduleRepository.save(schedule);
        return toResponse(schedule);
    }

    public DoctorScheduleResponse updateSchedule(Long id, DoctorScheduleRequest request) {
        DoctorSchedule schedule = doctorScheduleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("DoctorSchedule", "id", id));

        Doctor doctor = doctorRepository.findById(request.getDoctorId())
                .orElseThrow(() -> new ResourceNotFoundException("Doctor", "id", request.getDoctorId()));

        DayOfWeek dayOfWeek = parseDayOfWeek(request.getDayOfWeek());
        LocalTime startTime = LocalTime.parse(request.getStartTime());
        LocalTime endTime = LocalTime.parse(request.getEndTime());

        if (!endTime.isAfter(startTime)) {
            throw new BadRequestException("End time must be after start time");
        }

        if (!schedule.getDoctor().getId().equals(doctor.getId())
                || !schedule.getDayOfWeek().equals(dayOfWeek)) {
            if (doctorScheduleRepository.existsByDoctorIdAndDayOfWeek(doctor.getId(), dayOfWeek)) {
                throw new BadRequestException("Schedule already exists for " + dayOfWeek);
            }
        }

        schedule.setDoctor(doctor);
        schedule.setDayOfWeek(dayOfWeek);
        schedule.setStartTime(startTime);
        schedule.setEndTime(endTime);
        schedule.setAvailable(request.isAvailable());

        schedule = doctorScheduleRepository.save(schedule);
        return toResponse(schedule);
    }

    public void deleteSchedule(Long id) {
        DoctorSchedule schedule = doctorScheduleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("DoctorSchedule", "id", id));
        doctorScheduleRepository.delete(schedule);
    }

    @Transactional(readOnly = true)
    public boolean isDoctorAvailable(Long doctorId, DayOfWeek dayOfWeek, LocalTime time) {
        Optional<DoctorSchedule> schedule = doctorScheduleRepository
                .findByDoctorIdAndDayOfWeek(doctorId, dayOfWeek);
        if (schedule.isEmpty()) {
            return true;
        }
        return schedule.filter(DoctorSchedule::isAvailable)
                .filter(s -> !time.isBefore(s.getStartTime()) && !time.isAfter(s.getEndTime()))
                .isPresent();
    }

    private DayOfWeek parseDayOfWeek(String day) {
        try {
            return DayOfWeek.valueOf(day.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new BadRequestException("Invalid day of week: " + day
                    + ". Valid values: MONDAY, TUESDAY, WEDNESDAY, THURSDAY, FRIDAY, SATURDAY, SUNDAY");
        }
    }

    private DoctorScheduleResponse toResponse(DoctorSchedule schedule) {
        return DoctorScheduleResponse.builder()
                .id(schedule.getId())
                .doctorId(schedule.getDoctor().getId())
                .doctorName(schedule.getDoctor().getFirstName() + " " + schedule.getDoctor().getLastName())
                .dayOfWeek(schedule.getDayOfWeek().name())
                .startTime(schedule.getStartTime())
                .endTime(schedule.getEndTime())
                .isAvailable(schedule.isAvailable())
                .build();
    }
}
