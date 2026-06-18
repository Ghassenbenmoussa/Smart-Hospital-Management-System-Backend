package com.hospitalmanagement.mapper;

import com.hospitalmanagement.dto.request.MedicalRecordRequest;
import com.hospitalmanagement.dto.response.MedicalRecordResponse;
import com.hospitalmanagement.entity.MedicalRecord;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.InjectionStrategy;

@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface MedicalRecordMapper {

    @Mapping(target = "doctor", ignore = true)
    @Mapping(target = "patient", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    MedicalRecord toEntity(MedicalRecordRequest request);

    @Mapping(target = "doctorId", source = "doctor.id")
    @Mapping(target = "doctorName", source = "doctor", qualifiedByName = "doctorFullName")
    @Mapping(target = "patientId", source = "patient.id")
    @Mapping(target = "patientName", source = "patient", qualifiedByName = "patientFullName")
    MedicalRecordResponse toResponse(MedicalRecord record);

    @Named("doctorFullName")
    default String doctorFullName(com.hospitalmanagement.entity.Doctor doctor) {
        if (doctor == null) return null;
        return doctor.getFirstName() + " " + doctor.getLastName();
    }

    @Named("patientFullName")
    default String patientFullName(com.hospitalmanagement.entity.Patient patient) {
        if (patient == null) return null;
        return patient.getFirstName() + " " + patient.getLastName();
    }
}
