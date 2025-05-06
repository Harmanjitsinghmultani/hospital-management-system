package com.example.authentication_service.client;

import com.example.authentication_service.dto.DoctorDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "doctor-service")
public interface DoctorClient {
    @PostMapping("/doctors")
    void createDoctor(@RequestBody DoctorDTO doctorDTO);
}
