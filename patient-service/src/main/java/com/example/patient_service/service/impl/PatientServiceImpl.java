package com.example.patient_service.service.impl;

import com.example.patient_service.model.Patient;
import com.example.patient_service.repository.PatientRepository;
import com.example.patient_service.service.PatientService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PatientServiceImpl implements PatientService {
    private static final Logger logger = LoggerFactory.getLogger(PatientServiceImpl.class);
    private final PatientRepository patientRepository;

    @Override
    public Patient createPatient(Patient patient) {
        logger.debug("Creating patient: {}", patient);
        return patientRepository.save(patient);
    }

    @Override
    public Patient getPatientById(Long id) {
        logger.debug("Fetching patient by ID: {}", id);
        return patientRepository.findById(id)
                .orElseThrow(() -> {
                    logger.error("Patient not found with ID: {}", id);
                    throw new RuntimeException("Patient not found with ID: " + id);
                });
    }

    @Override
    public List<Patient> getAllPatients() {
        logger.debug("Fetching all patients");
        return patientRepository.findAll();
    }

    @Override
    public Patient updatePatient(Long id, Patient patientDetails) {
        logger.debug("Updating patient with ID: {}, data: {}", id, patientDetails);
        Patient patient = getPatientById(id);

        // Update only non-null fields
        if (patientDetails.getName() != null) {
            patient.setName(patientDetails.getName());
        }
        if (patientDetails.getAge() != 0) {
            patient.setAge(patientDetails.getAge());
        }
        if (patientDetails.getGender() != null) {
            patient.setGender(patientDetails.getGender());
        }
        if (patientDetails.getContactNumber() != null) {
            patient.setContactNumber(patientDetails.getContactNumber());
        }
        if (patientDetails.getEmail() != null) {
            patient.setEmail(patientDetails.getEmail());
        }
        if (patientDetails.getAddress() != null) {
            patient.setAddress(patientDetails.getAddress());
        }

        return patientRepository.save(patient);
    }

    @Override
    public void deletePatient(Long id) {
        logger.debug("Deleting patient with ID: {}", id);
        patientRepository.deleteById(id);
    }
}