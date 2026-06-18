package com.hospitalmanagement.config;

import com.hospitalmanagement.entity.Role;
import com.hospitalmanagement.entity.Speciality;
import com.hospitalmanagement.entity.User;
import com.hospitalmanagement.enums.RoleName;
import com.hospitalmanagement.repository.RoleRepository;
import com.hospitalmanagement.repository.SpecialityRepository;
import com.hospitalmanagement.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;

@Component
public class DataInitializer implements CommandLineRunner {

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final SpecialityRepository specialityRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(RoleRepository roleRepository,
                           UserRepository userRepository,
                           SpecialityRepository specialityRepository,
                           PasswordEncoder passwordEncoder) {
        this.roleRepository = roleRepository;
        this.userRepository = userRepository;
        this.specialityRepository = specialityRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        for (RoleName roleName : RoleName.values()) {
            if (roleRepository.findByName(roleName).isEmpty()) {
                roleRepository.save(Role.builder().name(roleName).build());
            }
        }

        if (userRepository.findByUsername("admin").isEmpty()) {
            Role adminRole = roleRepository.findByName(RoleName.ROLE_ADMIN)
                    .orElseThrow(() -> new RuntimeException("ROLE_ADMIN not found"));

            User admin = User.builder()
                    .username("admin")
                    .email("admin@hospital.com")
                    .password(passwordEncoder.encode("admin123"))
                    .enabled(true)
                    .passwordChanged(true)
                    .roles(Set.of(adminRole))
                    .build();
            userRepository.save(admin);
        }

        if (specialityRepository.count() == 0) {
            List<String> defaultSpecialities = List.of(
                    "Cardiology", "Dermatology", "Neurology", "Pediatrics",
                    "Orthopedics", "Radiology", "Surgery", "Ophthalmology",
                    "Psychiatry", "Anesthesiology"
            );
            for (String name : defaultSpecialities) {
                specialityRepository.save(Speciality.builder().name(name).build());
            }
        }
    }
}
