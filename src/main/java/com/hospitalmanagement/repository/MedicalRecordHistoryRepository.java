package com.hospitalmanagement.repository;

import com.hospitalmanagement.entity.MedicalRecordHistory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MedicalRecordHistoryRepository extends JpaRepository<MedicalRecordHistory, Long> {

    List<MedicalRecordHistory> findByMedicalRecordIdOrderByVersionDateDesc(Long medicalRecordId);

    Page<MedicalRecordHistory> findByMedicalRecordIdOrderByVersionDateDesc(Long medicalRecordId, Pageable pageable);
}
