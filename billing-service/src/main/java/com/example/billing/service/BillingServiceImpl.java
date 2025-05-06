package com.example.billing.service;

import com.example.billing.client.DoctorClient;
import com.example.billing.client.DoctorResponse;
import com.example.billing.client.PatientClient;
import com.example.billing.client.PatientResponse;
import com.example.billing.model.Bill;
import com.example.billing.repository.BillingRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BillingServiceImpl implements BillingService {

    private static final Logger logger = LoggerFactory.getLogger(BillingServiceImpl.class);

    private final BillingRepository billingRepository;
    private final PatientClient patientClient;
    private final DoctorClient doctorClient;

    @Autowired
    public BillingServiceImpl(BillingRepository billingRepository,
                              PatientClient patientClient,
                              DoctorClient doctorClient) {
        this.billingRepository = billingRepository;
        this.patientClient = patientClient;
        this.doctorClient = doctorClient;
    }

    @Override
    public Bill saveBill(Bill bill) {
        logger.debug("Validating patient and doctor before saving bill: {}", bill);

        // Validate patient existence
        try {
            PatientResponse patient = patientClient.getPatientById(bill.getPatientId());
            if (patient == null || patient.getName() == null) {
                throw new RuntimeException("Patient not found with ID: " + bill.getPatientId());
            }
            bill.setPatientName(patient.getName()); // ✅ SET name
        } catch (Exception e) {
            logger.error("Patient validation failed: {}", e.getMessage());
            throw new RuntimeException("Patient not found with ID: " + bill.getPatientId());
        }

        // Validate doctor existence
        try {
            DoctorResponse doctor = doctorClient.getDoctorById(bill.getDoctorId());
            if (doctor == null || doctor.getName() == null) {
                throw new RuntimeException("Doctor not found with ID: " + bill.getDoctorId());
            }
            bill.setDoctorName(doctor.getName()); // ✅ SET name
        } catch (Exception e) {
            logger.error("Doctor validation failed: {}", e.getMessage());
            throw new RuntimeException("Doctor not found with ID: " + bill.getDoctorId());
        }

        logger.debug("Saving validated bill: {}", bill);
        return billingRepository.save(bill);
    }

    @Override
    public List<Bill> getAllBills() {
        logger.debug("Fetching all bills");
        return billingRepository.findAll();
    }

    @Override
    public Bill getBillById(Long id) {
        logger.debug("Fetching bill by ID: {}", id);
        return billingRepository.findById(id)
                .orElseThrow(() -> {
                    logger.error("Bill not found with ID: {}", id);
                    return new RuntimeException("Bill not found with ID: " + id);
                });
    }

    @Override
    public List<Bill> getBillsByPatientId(Long patientId) {
        logger.debug("Fetching bills for patient ID: {}", patientId);
        return billingRepository.findByPatientId(patientId);
    }

    @Override
    public List<Bill> getBillsByDoctorId(Long doctorId) {
        logger.debug("Fetching bills for doctor ID: {}", doctorId);
        return billingRepository.findByDoctorId(doctorId);
    }

    @Override
    public void deleteBill(Long id) {
        logger.debug("Deleting bill with ID: {}", id);
        billingRepository.deleteById(id);
    }

    @Override
    public Bill updateBillStatus(Long id, String status) {
        logger.debug("Updating status for bill ID: {} to status: {}", id, status);
        Bill bill = billingRepository.findById(id).orElse(null);
        if (bill != null) {
            bill.setStatus(status);
            return billingRepository.save(bill);
        }
        logger.error("Bill not found with ID: {}", id);
        throw new RuntimeException("Bill not found with ID: " + id);
    }
}
