package com.hospitalmanagement.mapper;

import com.hospitalmanagement.dto.request.PatientRequest;
import com.hospitalmanagement.dto.response.PatientResponse;
import com.hospitalmanagement.entity.Patient;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-06-18T15:00:33+0100",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 17.0.2 (Oracle Corporation)"
)
@Component
public class PatientMapperImpl implements PatientMapper {

    @Override
    public Patient toEntity(PatientRequest request) {
        if ( request == null ) {
            return null;
        }

        Patient.PatientBuilder patient = Patient.builder();

        patient.birthDate( parseBirthDate( request.getBirthDate() ) );
        patient.gender( parseGender( request.getGender() ) );
        patient.firstName( request.getFirstName() );
        patient.lastName( request.getLastName() );
        patient.phone( request.getPhone() );
        patient.address( request.getAddress() );
        patient.emergencyContact( request.getEmergencyContact() );

        return patient.build();
    }

    @Override
    public PatientResponse toResponse(Patient patient) {
        if ( patient == null ) {
            return null;
        }

        PatientResponse.PatientResponseBuilder patientResponse = PatientResponse.builder();

        patientResponse.gender( genderToString( patient.getGender() ) );
        patientResponse.id( patient.getId() );
        patientResponse.firstName( patient.getFirstName() );
        patientResponse.lastName( patient.getLastName() );
        patientResponse.birthDate( patient.getBirthDate() );
        patientResponse.phone( patient.getPhone() );
        patientResponse.address( patient.getAddress() );
        patientResponse.emergencyContact( patient.getEmergencyContact() );
        patientResponse.createdAt( patient.getCreatedAt() );

        return patientResponse.build();
    }

    @Override
    public void updateEntity(PatientRequest request, Patient patient) {
        if ( request == null ) {
            return;
        }

        patient.setBirthDate( parseBirthDate( request.getBirthDate() ) );
        patient.setGender( parseGender( request.getGender() ) );
        patient.setFirstName( request.getFirstName() );
        patient.setLastName( request.getLastName() );
        patient.setPhone( request.getPhone() );
        patient.setAddress( request.getAddress() );
        patient.setEmergencyContact( request.getEmergencyContact() );
    }
}
