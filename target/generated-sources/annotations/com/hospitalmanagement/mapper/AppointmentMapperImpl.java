package com.hospitalmanagement.mapper;

import com.hospitalmanagement.dto.request.AppointmentRequest;
import com.hospitalmanagement.dto.response.AppointmentResponse;
import com.hospitalmanagement.entity.Appointment;
import com.hospitalmanagement.entity.Doctor;
import com.hospitalmanagement.entity.Patient;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-06-18T15:00:32+0100",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 17.0.2 (Oracle Corporation)"
)
@Component
public class AppointmentMapperImpl implements AppointmentMapper {

    @Override
    public Appointment toEntity(AppointmentRequest request) {
        if ( request == null ) {
            return null;
        }

        Appointment.AppointmentBuilder appointment = Appointment.builder();

        appointment.appointmentDate( parseDateTime( request.getAppointmentDate() ) );
        appointment.notes( request.getNotes() );

        return appointment.build();
    }

    @Override
    public AppointmentResponse toResponse(Appointment appointment) {
        if ( appointment == null ) {
            return null;
        }

        AppointmentResponse.AppointmentResponseBuilder appointmentResponse = AppointmentResponse.builder();

        appointmentResponse.doctorId( appointmentDoctorId( appointment ) );
        appointmentResponse.doctorName( fullName( appointment.getDoctor() ) );
        appointmentResponse.patientId( appointmentPatientId( appointment ) );
        appointmentResponse.patientName( fullName( appointment.getPatient() ) );
        appointmentResponse.status( statusToString( appointment.getStatus() ) );
        appointmentResponse.id( appointment.getId() );
        appointmentResponse.appointmentDate( appointment.getAppointmentDate() );
        appointmentResponse.notes( appointment.getNotes() );
        appointmentResponse.createdAt( appointment.getCreatedAt() );

        return appointmentResponse.build();
    }

    private Long appointmentDoctorId(Appointment appointment) {
        if ( appointment == null ) {
            return null;
        }
        Doctor doctor = appointment.getDoctor();
        if ( doctor == null ) {
            return null;
        }
        Long id = doctor.getId();
        if ( id == null ) {
            return null;
        }
        return id;
    }

    private Long appointmentPatientId(Appointment appointment) {
        if ( appointment == null ) {
            return null;
        }
        Patient patient = appointment.getPatient();
        if ( patient == null ) {
            return null;
        }
        Long id = patient.getId();
        if ( id == null ) {
            return null;
        }
        return id;
    }
}
