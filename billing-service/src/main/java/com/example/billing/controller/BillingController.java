package com.example.billing.controller;

import com.example.billing.model.Bill;
import com.example.billing.service.BillingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/bills")
@CrossOrigin(origins = "*")
public class BillingController {

    @Autowired
    private BillingService billingService;

    @PostMapping
    public Bill createBill(@RequestBody Bill bill) {
        return billingService.saveBill(bill);
    }

    @GetMapping
    public List<Bill> getAllBills() {
        return billingService.getAllBills();
    }

    @GetMapping("/{id}")
    public Bill getBillById(@PathVariable Long id) {
        return billingService.getBillById(id);
    }

    @GetMapping("/patient/{patientId}")
    public List<Bill> getBillsByPatientId(@PathVariable Long patientId) {
        return billingService.getBillsByPatientId(patientId);
    }

    @GetMapping("/doctor/{doctorId}")
    public List<Bill> getBillsByDoctorId(@PathVariable Long doctorId) {
        return billingService.getBillsByDoctorId(doctorId);
    }

    @DeleteMapping("/{id}")
    public String deleteBill(@PathVariable Long id) {
        billingService.deleteBill(id);
        return "Bill deleted successfully!";
    }

    @PatchMapping("/{id}/status")
    public Bill updateBillStatus(@PathVariable Long id, @RequestParam String status) {
        return billingService.updateBillStatus(id, status);
    }
}