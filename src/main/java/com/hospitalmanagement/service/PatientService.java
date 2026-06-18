package com.hospitalmanagement.service;

import com.hospitalmanagement.dto.request.PatientRequest;
import com.hospitalmanagement.dto.response.PatientResponse;
import com.hospitalmanagement.dto.response.PageResponse;
import com.hospitalmanagement.entity.Patient;
import com.hospitalmanagement.exception.ResourceNotFoundException;
import com.hospitalmanagement.mapper.PatientMapper;
import com.hospitalmanagement.repository.PatientRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class PatientService {

    private final PatientRepository patientRepository;
    private final PatientMapper patientMapper;

    public PatientService(PatientRepository patientRepository, PatientMapper patientMapper) {
        this.patientRepository = patientRepository;
        this.patientMapper = patientMapper;
    }

    @Transactional(readOnly = true)
    public PageResponse<PatientResponse> getAllPatients(Pageable pageable, String search) {
        Page<Patient> page;
        if (search != null && !search.isBlank()) {
            page = patientRepository.findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCase(
                    search, search, pageable);
        } else {
            page = patientRepository.findAll(pageable);
        }
        return buildPageResponse(page);
    }

    @Transactional(readOnly = true)
    public PatientResponse getPatientById(Long id) {
        Patient patient = findPatientById(id);
        return patientMapper.toResponse(patient);
    }

    public PatientResponse createPatient(PatientRequest request) {
        Patient patient = patientMapper.toEntity(request);
        patient = patientRepository.save(patient);
        return patientMapper.toResponse(patient);
    }

    public PatientResponse updatePatient(Long id, PatientRequest request) {
        Patient patient = findPatientById(id);
        patientMapper.updateEntity(request, patient);
        patient = patientRepository.save(patient);
        return patientMapper.toResponse(patient);
    }

    public void deletePatient(Long id) {
        Patient patient = findPatientById(id);
        patientRepository.delete(patient);
    }

    @Transactional(readOnly = true)
    public long getTotalCount() {
        return patientRepository.count();
    }

    private Patient findPatientById(Long id) {
        return patientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Patient", "id", id));
    }

    private PageResponse<PatientResponse> buildPageResponse(Page<Patient> page) {
        return PageResponse.<PatientResponse>builder()
                .content(page.getContent().stream().map(patientMapper::toResponse).toList())
                .page(page.getNumber())
                .size(page.getSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .last(page.isLast())
                .build();
    }
}
