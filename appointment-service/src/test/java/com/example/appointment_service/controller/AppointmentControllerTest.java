package com.example.appointment_service.controller;

import com.example.appointment_service.model.AppointmentRequest;
import com.example.appointment_service.model.AppointmentResponse;
import com.example.appointment_service.service.AppointmentService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AppointmentControllerTest {

    @Mock
    private AppointmentService appointmentService;

    @InjectMocks
    private AppointmentController appointmentController;

    private final UserDetails userDetails = new User("testuser", "password", Collections.emptyList());

    @Test
    void createAppointment_ShouldReturnCreatedAppointment() {
        AppointmentRequest request = new AppointmentRequest();
        request.setPatientId(1L);
        request.setDoctorId(2L);
        request.setAppointmentTime(LocalDateTime.now().plusHours(1));
        request.setReason("Checkup");

        AppointmentResponse response = AppointmentResponse.builder()
                .id(1L)
                .patientId(1L)
                .doctorId(2L)
                .appointmentTime(request.getAppointmentTime())
                .reason("Checkup")
                .status("SCHEDULED")
                .build();

        when(appointmentService.createAppointment(any(AppointmentRequest.class), any(String.class)))
                .thenReturn(response);

        ResponseEntity<AppointmentResponse> result = appointmentController.createAppointment(request, userDetails);

        assertEquals(response, result.getBody());
    }

    @Test
    void getAppointmentById_ShouldReturnAppointment() {
        Long appointmentId = 1L;
        AppointmentResponse response = AppointmentResponse.builder()
                .id(appointmentId)
                .patientId(1L)
                .doctorId(2L)
                .appointmentTime(LocalDateTime.now().plusHours(1))
                .reason("Checkup")
                .status("SCHEDULED")
                .build();

        when(appointmentService.getAppointmentById(appointmentId))
                .thenReturn(response);

        ResponseEntity<AppointmentResponse> result = appointmentController.getAppointmentById(appointmentId);

        assertEquals(response, result.getBody());
    }

    @Test
    void getAllAppointments_ShouldReturnAllAppointments() {
        AppointmentResponse response = AppointmentResponse.builder()
                .id(1L)
                .patientId(1L)
                .doctorId(2L)
                .appointmentTime(LocalDateTime.now().plusHours(1))
                .reason("Checkup")
                .status("SCHEDULED")
                .build();

        List<AppointmentResponse> responses = List.of(response);
        Page<AppointmentResponse> page = new PageImpl<>(responses);

        when(appointmentService.getAllAppointmentsPaginated(0, 10, "appointmentTime", "asc"))
                .thenReturn(page);

        ResponseEntity<Page<AppointmentResponse>> result =
                appointmentController.getAllAppointments(0, 10, "appointmentTime", "asc");

        assertEquals(page, result.getBody());
    }
}