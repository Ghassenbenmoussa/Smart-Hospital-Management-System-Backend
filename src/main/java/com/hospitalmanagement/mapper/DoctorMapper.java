package com.hospitalmanagement.mapper;

import com.hospitalmanagement.dto.request.DoctorRequest;
import com.hospitalmanagement.dto.response.DoctorResponse;
import com.hospitalmanagement.entity.Doctor;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.InjectionStrategy;

@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface DoctorMapper {

    Doctor toEntity(DoctorRequest request);

    DoctorResponse toResponse(Doctor doctor);

    void updateEntity(DoctorRequest request, @MappingTarget Doctor doctor);
}
