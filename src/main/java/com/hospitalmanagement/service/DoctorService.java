package com.hospitalmanagement.service;

import com.hospitalmanagement.dto.request.DoctorRequest;
import com.hospitalmanagement.dto.response.DoctorResponse;
import com.hospitalmanagement.dto.response.PageResponse;
import com.hospitalmanagement.entity.Doctor;
import com.hospitalmanagement.entity.Role;
import com.hospitalmanagement.entity.User;
import com.hospitalmanagement.enums.RoleName;
import com.hospitalmanagement.exception.BadRequestException;
import com.hospitalmanagement.exception.ResourceNotFoundException;
import com.hospitalmanagement.mapper.DoctorMapper;
import com.hospitalmanagement.repository.DoctorRepository;
import com.hospitalmanagement.repository.RoleRepository;
import com.hospitalmanagement.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

@Service
@Transactional
public class DoctorService {

    private final DoctorRepository doctorRepository;
    private final DoctorMapper doctorMapper;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public DoctorService(DoctorRepository doctorRepository, DoctorMapper doctorMapper,
                         UserRepository userRepository, RoleRepository roleRepository,
                         PasswordEncoder passwordEncoder) {
        this.doctorRepository = doctorRepository;
        this.doctorMapper = doctorMapper;
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional(readOnly = true)
    public PageResponse<DoctorResponse> getAllDoctors(Pageable pageable, String search) {
        Page<Doctor> page;
        if (search != null && !search.isBlank()) {
            page = doctorRepository.findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCase(
                    search, search, pageable);
        } else {
            page = doctorRepository.findAll(pageable);
        }
        return buildPageResponse(page);
    }

    @Transactional(readOnly = true)
    public DoctorResponse getDoctorById(Long id) {
        Doctor doctor = findDoctorById(id);
        return doctorMapper.toResponse(doctor);
    }

    public DoctorResponse createDoctor(DoctorRequest request) {
        if (doctorRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new BadRequestException("Doctor with email " + request.getEmail() + " already exists");
        }
        if (doctorRepository.findByLicenseNumber(request.getLicenseNumber()).isPresent()) {
            throw new BadRequestException("Doctor with license number " + request.getLicenseNumber() + " already exists");
        }
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BadRequestException("User with email " + request.getEmail() + " already exists");
        }

        Doctor doctor = doctorMapper.toEntity(request);
        doctor = doctorRepository.save(doctor);

        String tempPassword = "temp123";
        Role doctorRole = roleRepository.findByName(RoleName.ROLE_DOCTOR)
                .orElseThrow(() -> new ResourceNotFoundException("Role", "name", RoleName.ROLE_DOCTOR));

        User user = User.builder()
                .username(request.getEmail())
                .email(request.getEmail())
                .password(passwordEncoder.encode(tempPassword))
                .enabled(true)
                .passwordChanged(false)
                .roles(Set.of(doctorRole))
                .build();
        userRepository.save(user);

        DoctorResponse response = doctorMapper.toResponse(doctor);
        response.setTempPassword(tempPassword);
        return response;
    }

    public DoctorResponse updateDoctor(Long id, DoctorRequest request) {
        Doctor doctor = findDoctorById(id);
        if (!doctor.getEmail().equals(request.getEmail())
                && doctorRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new BadRequestException("Doctor with email " + request.getEmail() + " already exists");
        }
        if (!doctor.getLicenseNumber().equals(request.getLicenseNumber())
                && doctorRepository.findByLicenseNumber(request.getLicenseNumber()).isPresent()) {
            throw new BadRequestException("Doctor with license number " + request.getLicenseNumber() + " already exists");
        }
        doctorMapper.updateEntity(request, doctor);
        doctor = doctorRepository.save(doctor);
        return doctorMapper.toResponse(doctor);
    }

    public void deleteDoctor(Long id) {
        Doctor doctor = findDoctorById(id);
        userRepository.findByEmail(doctor.getEmail()).ifPresent(userRepository::delete);
        doctorRepository.delete(doctor);
    }

    @Transactional(readOnly = true)
    public long getTotalCount() {
        return doctorRepository.count();
    }

    private Doctor findDoctorById(Long id) {
        return doctorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Doctor", "id", id));
    }

    private PageResponse<DoctorResponse> buildPageResponse(Page<Doctor> page) {
        return PageResponse.<DoctorResponse>builder()
                .content(page.getContent().stream().map(doctorMapper::toResponse).toList())
                .page(page.getNumber())
                .size(page.getSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .last(page.isLast())
                .build();
    }
}
