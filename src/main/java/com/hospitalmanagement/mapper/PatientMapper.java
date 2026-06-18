package com.hospitalmanagement.mapper;

import com.hospitalmanagement.dto.request.PatientRequest;
import com.hospitalmanagement.dto.response.PatientResponse;
import com.hospitalmanagement.entity.Patient;
import com.hospitalmanagement.enums.Gender;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Named;

import java.time.LocalDate;
@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface PatientMapper {

    @Mapping(target = "birthDate", source = "birthDate", qualifiedByName = "parseBirthDate")
    @Mapping(target = "gender", source = "gender", qualifiedByName = "parseGender")
    Patient toEntity(PatientRequest request);

    @Mapping(target = "gender", source = "gender", qualifiedByName = "genderToString")
    PatientResponse toResponse(Patient patient);

    @Mapping(target = "birthDate", source = "birthDate", qualifiedByName = "parseBirthDate")
    @Mapping(target = "gender", source = "gender", qualifiedByName = "parseGender")
    void updateEntity(PatientRequest request, @MappingTarget Patient patient);

    @Named("parseBirthDate")
    default LocalDate parseBirthDate(String birthDate) {
        if (birthDate == null || birthDate.isBlank()) {
            return null;
        }
        return LocalDate.parse(birthDate.substring(0, 10));
    }

    @Named("parseGender")
    default Gender parseGender(String gender) {
        if (gender == null || gender.isBlank()) {
            return null;
        }
        try {
            return Gender.valueOf(gender.toUpperCase());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    @Named("genderToString")
    default String genderToString(Gender gender) {
        if (gender == null) {
            return null;
        }
        return gender.name();
    }
}
