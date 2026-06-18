package com.hospitalmanagement.service;

import com.hospitalmanagement.dto.request.MedicalRecordRequest;
import com.hospitalmanagement.dto.response.MedicalRecordHistoryResponse;
import com.hospitalmanagement.dto.response.MedicalRecordResponse;
import com.hospitalmanagement.dto.response.PageResponse;
import com.hospitalmanagement.entity.Doctor;
import com.hospitalmanagement.entity.MedicalRecord;
import com.hospitalmanagement.entity.MedicalRecordHistory;
import com.hospitalmanagement.entity.Patient;
import com.hospitalmanagement.exception.ResourceNotFoundException;
import com.hospitalmanagement.mapper.MedicalRecordMapper;
import com.hospitalmanagement.repository.DoctorRepository;
import com.hospitalmanagement.repository.MedicalRecordHistoryRepository;
import com.hospitalmanagement.repository.MedicalRecordRepository;
import com.hospitalmanagement.repository.PatientRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class MedicalRecordService {

    private final MedicalRecordRepository medicalRecordRepository;
    private final MedicalRecordHistoryRepository medicalRecordHistoryRepository;
    private final DoctorRepository doctorRepository;
    private final PatientRepository patientRepository;
    private final MedicalRecordMapper medicalRecordMapper;

    public MedicalRecordService(MedicalRecordRepository medicalRecordRepository,
                                MedicalRecordHistoryRepository medicalRecordHistoryRepository,
                                DoctorRepository doctorRepository,
                                PatientRepository patientRepository,
                                MedicalRecordMapper medicalRecordMapper) {
        this.medicalRecordRepository = medicalRecordRepository;
        this.medicalRecordHistoryRepository = medicalRecordHistoryRepository;
        this.doctorRepository = doctorRepository;
        this.patientRepository = patientRepository;
        this.medicalRecordMapper = medicalRecordMapper;
    }

    @Transactional(readOnly = true)
    public PageResponse<MedicalRecordResponse> getAllMedicalRecords(Pageable pageable) {
        Page<MedicalRecord> page = medicalRecordRepository.findAll(pageable);
        return buildPageResponse(page);
    }

    @Transactional(readOnly = true)
    public MedicalRecordResponse getMedicalRecordById(Long id) {
        MedicalRecord record = findMedicalRecordById(id);
        return medicalRecordMapper.toResponse(record);
    }

    public MedicalRecordResponse createMedicalRecord(MedicalRecordRequest request) {
        Patient patient = patientRepository.findById(request.getPatientId())
                .orElseThrow(() -> new ResourceNotFoundException("Patient", "id", request.getPatientId()));
        Doctor doctor = doctorRepository.findById(request.getDoctorId())
                .orElseThrow(() -> new ResourceNotFoundException("Doctor", "id", request.getDoctorId()));

        MedicalRecord record = medicalRecordMapper.toEntity(request);
        record.setPatient(patient);
        record.setDoctor(doctor);

        record = medicalRecordRepository.save(record);
        return medicalRecordMapper.toResponse(record);
    }

    public MedicalRecordResponse updateMedicalRecord(Long id, MedicalRecordRequest request) {
        MedicalRecord record = findMedicalRecordById(id);
        Patient patient = patientRepository.findById(request.getPatientId())
                .orElseThrow(() -> new ResourceNotFoundException("Patient", "id", request.getPatientId()));
        Doctor doctor = doctorRepository.findById(request.getDoctorId())
                .orElseThrow(() -> new ResourceNotFoundException("Doctor", "id", request.getDoctorId()));

        MedicalRecordHistory history = MedicalRecordHistory.builder()
                .medicalRecord(record)
                .diagnosis(record.getDiagnosis())
                .treatment(record.getTreatment())
                .notes(record.getNotes())
                .build();
        medicalRecordHistoryRepository.save(history);

        MedicalRecord updated = medicalRecordMapper.toEntity(request);
        updated.setId(id);
        updated.setPatient(patient);
        updated.setDoctor(doctor);
        updated.setCreatedAt(record.getCreatedAt());

        updated = medicalRecordRepository.save(updated);
        return medicalRecordMapper.toResponse(updated);
    }

    @Transactional(readOnly = true)
    public PageResponse<MedicalRecordHistoryResponse> getMedicalRecordHistory(Long medicalRecordId, Pageable pageable) {
        if (!medicalRecordRepository.existsById(medicalRecordId)) {
            throw new ResourceNotFoundException("MedicalRecord", "id", medicalRecordId);
        }
        Page<MedicalRecordHistory> page = medicalRecordHistoryRepository
                .findByMedicalRecordIdOrderByVersionDateDesc(medicalRecordId, pageable);
        return PageResponse.<MedicalRecordHistoryResponse>builder()
                .content(page.getContent().stream()
                        .map(h -> MedicalRecordHistoryResponse.builder()
                                .id(h.getId())
                                .medicalRecordId(h.getMedicalRecord().getId())
                                .diagnosis(h.getDiagnosis())
                                .treatment(h.getTreatment())
                                .notes(h.getNotes())
                                .versionDate(h.getVersionDate())
                                .build())
                        .toList())
                .page(page.getNumber())
                .size(page.getSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .last(page.isLast())
                .build();
    }

    public void deleteMedicalRecord(Long id) {
        MedicalRecord record = findMedicalRecordById(id);
        medicalRecordRepository.delete(record);
    }

    @Transactional(readOnly = true)
    public PageResponse<MedicalRecordResponse> getMedicalRecordsByPatient(Long patientId, Pageable pageable) {
        if (!patientRepository.existsById(patientId)) {
            throw new ResourceNotFoundException("Patient", "id", patientId);
        }
        Page<MedicalRecord> page = medicalRecordRepository.findByPatientId(patientId, pageable);
        return buildPageResponse(page);
    }

    @Transactional(readOnly = true)
    public long getTotalCount() {
        return medicalRecordRepository.count();
    }

    private MedicalRecord findMedicalRecordById(Long id) {
        return medicalRecordRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("MedicalRecord", "id", id));
    }

    private PageResponse<MedicalRecordResponse> buildPageResponse(Page<MedicalRecord> page) {
        return PageResponse.<MedicalRecordResponse>builder()
                .content(page.getContent().stream().map(medicalRecordMapper::toResponse).toList())
                .page(page.getNumber())
                .size(page.getSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .last(page.isLast())
                .build();
    }
}
