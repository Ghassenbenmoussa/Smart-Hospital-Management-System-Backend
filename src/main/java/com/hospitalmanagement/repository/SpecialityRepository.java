package com.hospitalmanagement.repository;

import com.hospitalmanagement.entity.Speciality;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SpecialityRepository extends JpaRepository<Speciality, Long> {
    Optional<Speciality> findByName(String name);
    boolean existsByName(String name);
}
