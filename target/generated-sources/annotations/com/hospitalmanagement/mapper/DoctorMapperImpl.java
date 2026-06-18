package com.hospitalmanagement.mapper;

import com.hospitalmanagement.dto.request.DoctorRequest;
import com.hospitalmanagement.dto.response.DoctorResponse;
import com.hospitalmanagement.entity.Doctor;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-06-18T15:00:32+0100",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 17.0.2 (Oracle Corporation)"
)
@Component
public class DoctorMapperImpl implements DoctorMapper {

    @Override
    public Doctor toEntity(DoctorRequest request) {
        if ( request == null ) {
            return null;
        }

        Doctor.DoctorBuilder doctor = Doctor.builder();

        doctor.firstName( request.getFirstName() );
        doctor.lastName( request.getLastName() );
        doctor.email( request.getEmail() );
        doctor.phone( request.getPhone() );
        doctor.speciality( request.getSpeciality() );
        doctor.licenseNumber( request.getLicenseNumber() );

        return doctor.build();
    }

    @Override
    public DoctorResponse toResponse(Doctor doctor) {
        if ( doctor == null ) {
            return null;
        }

        DoctorResponse.DoctorResponseBuilder doctorResponse = DoctorResponse.builder();

        doctorResponse.id( doctor.getId() );
        doctorResponse.firstName( doctor.getFirstName() );
        doctorResponse.lastName( doctor.getLastName() );
        doctorResponse.email( doctor.getEmail() );
        doctorResponse.phone( doctor.getPhone() );
        doctorResponse.speciality( doctor.getSpeciality() );
        doctorResponse.licenseNumber( doctor.getLicenseNumber() );
        doctorResponse.createdAt( doctor.getCreatedAt() );

        return doctorResponse.build();
    }

    @Override
    public void updateEntity(DoctorRequest request, Doctor doctor) {
        if ( request == null ) {
            return;
        }

        doctor.setFirstName( request.getFirstName() );
        doctor.setLastName( request.getLastName() );
        doctor.setEmail( request.getEmail() );
        doctor.setPhone( request.getPhone() );
        doctor.setSpeciality( request.getSpeciality() );
        doctor.setLicenseNumber( request.getLicenseNumber() );
    }
}
