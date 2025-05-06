package com.example.appointment_service.client;

import com.example.appointment_service.dto.Doctor;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "doctor-service", url = "http://localhost:8086/doctors")
public interface DoctorClient {

    @GetMapping("/{id}")
    Doctor getDoctorById(@PathVariable("id") Long id);

    // ✅ Added this to support fetching doctor by userId
    @GetMapping("/user/{userId}")
    Doctor getDoctorByUserId(@PathVariable("userId") Long userId);
}
