package com.example.billing.service;

import com.example.billing.model.Bill;
import java.util.List;

public interface BillingService {
    Bill saveBill(Bill bill);
    List<Bill> getAllBills();
    Bill getBillById(Long id);
    List<Bill> getBillsByPatientId(Long patientId);
    List<Bill> getBillsByDoctorId(Long doctorId);
    void deleteBill(Long id);
    Bill updateBillStatus(Long id, String status);
}