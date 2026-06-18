package com.hospitalmanagement.mapper;

import com.hospitalmanagement.dto.request.MedicalRecordRequest;
import com.hospitalmanagement.dto.response.MedicalRecordResponse;
import com.hospitalmanagement.entity.Doctor;
import com.hospitalmanagement.entity.MedicalRecord;
import com.hospitalmanagement.entity.Patient;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-06-18T15:00:32+0100",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 17.0.2 (Oracle Corporation)"
)
@Component
public class MedicalRecordMapperImpl implements MedicalRecordMapper {

    @Override
    public MedicalRecord toEntity(MedicalRecordRequest request) {
        if ( request == null ) {
            return null;
        }

        MedicalRecord.MedicalRecordBuilder medicalRecord = MedicalRecord.builder();

        medicalRecord.diagnosis( request.getDiagnosis() );
        medicalRecord.treatment( request.getTreatment() );
        medicalRecord.notes( request.getNotes() );

        return medicalRecord.build();
    }

    @Override
    public MedicalRecordResponse toResponse(MedicalRecord record) {
        if ( record == null ) {
            return null;
        }

        MedicalRecordResponse.MedicalRecordResponseBuilder medicalRecordResponse = MedicalRecordResponse.builder();

        medicalRecordResponse.doctorId( recordDoctorId( record ) );
        medicalRecordResponse.doctorName( doctorFullName( record.getDoctor() ) );
        medicalRecordResponse.patientId( recordPatientId( record ) );
        medicalRecordResponse.patientName( patientFullName( record.getPatient() ) );
        medicalRecordResponse.id( record.getId() );
        medicalRecordResponse.diagnosis( record.getDiagnosis() );
        medicalRecordResponse.treatment( record.getTreatment() );
        medicalRecordResponse.notes( record.getNotes() );
        medicalRecordResponse.createdAt( record.getCreatedAt() );

        return medicalRecordResponse.build();
    }

    private Long recordDoctorId(MedicalRecord medicalRecord) {
        if ( medicalRecord == null ) {
            return null;
        }
        Doctor doctor = medicalRecord.getDoctor();
        if ( doctor == null ) {
            return null;
        }
        Long id = doctor.getId();
        if ( id == null ) {
            return null;
        }
        return id;
    }

    private Long recordPatientId(MedicalRecord medicalRecord) {
        if ( medicalRecord == null ) {
            return null;
        }
        Patient patient = medicalRecord.getPatient();
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
