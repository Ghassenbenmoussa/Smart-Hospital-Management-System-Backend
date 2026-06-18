package com.hospitalmanagement.mapper;

import com.hospitalmanagement.dto.request.AppointmentRequest;
import com.hospitalmanagement.dto.response.AppointmentResponse;
import com.hospitalmanagement.entity.Appointment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.InjectionStrategy;

import java.time.LocalDateTime;
import static com.hospitalmanagement.util.DateUtils.parseIsoDateTime;

@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface AppointmentMapper {

    @Mapping(target = "appointmentDate", source = "appointmentDate", qualifiedByName = "parseDateTime")
    @Mapping(target = "doctor", ignore = true)
    @Mapping(target = "patient", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    Appointment toEntity(AppointmentRequest request);

    @Mapping(target = "doctorId", source = "doctor.id")
    @Mapping(target = "doctorName", source = "doctor", qualifiedByName = "fullName")
    @Mapping(target = "patientId", source = "patient.id")
    @Mapping(target = "patientName", source = "patient", qualifiedByName = "fullName")
    @Mapping(target = "status", source = "status", qualifiedByName = "statusToString")
    AppointmentResponse toResponse(Appointment appointment);

    @Named("parseDateTime")
    default LocalDateTime parseDateTime(String dateTime) {
        return parseIsoDateTime(dateTime);
    }

    @Named("fullName")
    default String fullName(Object entity) {
        if (entity == null) {
            return null;
        }
        if (entity instanceof com.hospitalmanagement.entity.Doctor doctor) {
            return doctor.getFirstName() + " " + doctor.getLastName();
        }
        if (entity instanceof com.hospitalmanagement.entity.Patient patient) {
            return patient.getFirstName() + " " + patient.getLastName();
        }
        return null;
    }

    @Named("statusToString")
    default String statusToString(com.hospitalmanagement.enums.AppointmentStatus status) {
        if (status == null) {
            return null;
        }
        return status.name();
    }
}
