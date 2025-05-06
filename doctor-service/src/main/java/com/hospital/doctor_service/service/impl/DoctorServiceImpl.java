package com.hospital.doctor_service.service.impl;

import com.hospital.doctor_service.model.Doctor;
import com.hospital.doctor_service.repository.DoctorRepository;
import com.hospital.doctor_service.service.DoctorService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DoctorServiceImpl implements DoctorService {

    private static final Logger logger = LoggerFactory.getLogger(DoctorServiceImpl.class);
    private final DoctorRepository doctorRepository;

    @Autowired
    public DoctorServiceImpl(DoctorRepository doctorRepository) {
        this.doctorRepository = doctorRepository;
    }

    @Override
    public Doctor saveDoctor(Doctor doctor) {
        logger.debug("Saving doctor: {}", doctor);
        return doctorRepository.save(doctor);
    }

    @Override
    public List<Doctor> getAllDoctors() {
        logger.debug("Fetching all doctors");
        return doctorRepository.findAll();
    }

    @Override
    public Doctor getDoctorById(Long id) {
        logger.debug("Fetching doctor by ID: {}", id);
        return doctorRepository.findById(id)
                .orElseThrow(() -> {
                    logger.error("Doctor not found with ID: {}", id);
                    return new RuntimeException("Doctor not found with ID: " + id);
                });
    }

    @Override
    public Doctor getDoctorByUserId(Long userId) {
        logger.debug("Fetching doctor by userId: {}", userId);
        return doctorRepository.findByUserId(userId)
                .orElseThrow(() -> {
                    logger.error("Doctor not found with userId: {}", userId);
                    return new RuntimeException("Doctor not found with userId: " + userId);
                });
    }

    @Override
    public void deleteDoctor(Long id) {
        logger.debug("Deleting doctor with ID: {}", id);
        doctorRepository.deleteById(id);
    }
}
