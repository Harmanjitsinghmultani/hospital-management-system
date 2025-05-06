package com.hospital.doctor_service.service;

import com.hospital.doctor_service.model.Doctor;

import java.util.List;

public interface DoctorService {
    Doctor saveDoctor(Doctor doctor);
    List<Doctor> getAllDoctors();
    Doctor getDoctorById(Long id);
    Doctor getDoctorByUserId(Long userId); // ✅ Support fetching by userId
    void deleteDoctor(Long id);
}
