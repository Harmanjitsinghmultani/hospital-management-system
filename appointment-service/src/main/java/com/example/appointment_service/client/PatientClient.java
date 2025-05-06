package com.example.appointment_service.client;

import com.example.appointment_service.dto.Patient;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "patient-service", url = "http://localhost:8083/api/patients")
public interface PatientClient {

    @GetMapping("/{id}")
    Patient getPatientById(@PathVariable Long id);
}
