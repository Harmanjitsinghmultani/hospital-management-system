package com.hospital.doctor_service.repository;

import com.hospital.doctor_service.model.Doctor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DoctorRepository extends JpaRepository<Doctor, Long> {

    // ✅ This is required for your getDoctorByUserId logic to work
    Optional<Doctor> findByUserId(Long userId);
}
