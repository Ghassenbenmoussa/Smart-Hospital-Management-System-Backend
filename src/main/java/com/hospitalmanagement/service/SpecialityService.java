package com.hospitalmanagement.service;

import com.hospitalmanagement.dto.request.SpecialityRequest;
import com.hospitalmanagement.dto.response.SpecialityResponse;
import com.hospitalmanagement.entity.Speciality;
import com.hospitalmanagement.exception.BadRequestException;
import com.hospitalmanagement.exception.ResourceNotFoundException;
import com.hospitalmanagement.repository.SpecialityRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class SpecialityService {

    private final SpecialityRepository specialityRepository;

    public SpecialityService(SpecialityRepository specialityRepository) {
        this.specialityRepository = specialityRepository;
    }

    @Transactional(readOnly = true)
    public List<SpecialityResponse> getAll() {
        return specialityRepository.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public SpecialityResponse getById(Long id) {
        Speciality speciality = specialityRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Speciality", "id", id));
        return toResponse(speciality);
    }

    public SpecialityResponse create(SpecialityRequest request) {
        if (specialityRepository.existsByName(request.getName())) {
            throw new BadRequestException("Speciality '" + request.getName() + "' already exists");
        }
        Speciality speciality = Speciality.builder()
                .name(request.getName())
                .build();
        speciality = specialityRepository.save(speciality);
        return toResponse(speciality);
    }

    public SpecialityResponse update(Long id, SpecialityRequest request) {
        Speciality speciality = specialityRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Speciality", "id", id));
        if (!speciality.getName().equalsIgnoreCase(request.getName())
                && specialityRepository.existsByName(request.getName())) {
            throw new BadRequestException("Speciality '" + request.getName() + "' already exists");
        }
        speciality.setName(request.getName());
        speciality = specialityRepository.save(speciality);
        return toResponse(speciality);
    }

    public void delete(Long id) {
        Speciality speciality = specialityRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Speciality", "id", id));
        specialityRepository.delete(speciality);
    }

    private SpecialityResponse toResponse(Speciality speciality) {
        return SpecialityResponse.builder()
                .id(speciality.getId())
                .name(speciality.getName())
                .build();
    }
}
