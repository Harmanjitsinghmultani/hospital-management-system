package com.example.authentication_service.client;

import com.example.authentication_service.dto.PatientDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "patient-service")
public interface PatientClient {
    @PostMapping("/api/patients")
    void createPatient(@RequestBody PatientDTO patientDTO);
}
