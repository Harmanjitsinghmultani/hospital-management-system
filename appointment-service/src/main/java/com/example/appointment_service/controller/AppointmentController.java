package com.example.appointment_service.controller;

import com.example.appointment_service.model.AppointmentRequest;
import com.example.appointment_service.model.AppointmentResponse;
import com.example.appointment_service.service.AppointmentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/appointments")
@Tag(name = "Appointment Management", description = "Endpoints for managing medical appointments")
@SecurityRequirement(name = "bearerAuth")
public class AppointmentController {

    private static final Logger logger = LoggerFactory.getLogger(AppointmentController.class);
    private final AppointmentService appointmentService;

    @Autowired
    public AppointmentController(AppointmentService appointmentService) {
        this.appointmentService = appointmentService;
    }

    @PostMapping
    @PreAuthorize("hasRole('PATIENT')")
    @Operation(summary = "Create new appointment", description = "Allows patients to book new appointments")
    public ResponseEntity<AppointmentResponse> createAppointment(
            @Valid @RequestBody AppointmentRequest appointmentRequest,
            @AuthenticationPrincipal UserDetails userDetails) {
        logger.info("Creating new appointment for user: {}", userDetails.getUsername());
        AppointmentResponse response = appointmentService.createAppointment(appointmentRequest, userDetails.getUsername());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('PATIENT', 'DOCTOR', 'ADMIN')")
    @Operation(summary = "Get appointment by ID", description = "Retrieve appointment details")
    public ResponseEntity<AppointmentResponse> getAppointmentById(@PathVariable Long id) {
        logger.debug("Fetching appointment with ID: {}", id);
        return ResponseEntity.ok(appointmentService.getAppointmentById(id));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR')")
    @Operation(summary = "Get paginated appointments", description = "Retrieve paginated list with sorting")
    public ResponseEntity<Page<AppointmentResponse>> getAllAppointments(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "appointmentTime") String sortBy,
            @RequestParam(defaultValue = "asc") String direction) {
        logger.info("Fetching appointments - page: {}, size: {}, sort: {}, direction: {}", page, size, sortBy, direction);
        return ResponseEntity.ok(appointmentService.getAllAppointmentsPaginated(page, size, sortBy, direction));
    }

    @GetMapping("/all")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get all appointments", description = "Retrieve all appointments (no pagination)")
    public ResponseEntity<List<AppointmentResponse>> getAllAppointmentsUnpaginated() {
        logger.info("Fetching all appointments (unpaginated)");
        return ResponseEntity.ok(appointmentService.getAllAppointments());
    }

    @GetMapping("/patient/{patientId}")
    @PreAuthorize("hasAnyRole('PATIENT', 'DOCTOR', 'ADMIN')")
    @Operation(summary = "Get appointments by patient ID", description = "Retrieve appointments for specific patient")
    public ResponseEntity<List<AppointmentResponse>> getAppointmentsByPatientId(@PathVariable Long patientId) {
        logger.info("Fetching appointments for patient ID: {}", patientId);
        return ResponseEntity.ok(appointmentService.getAppointmentsByPatientId(patientId));
    }

    @GetMapping("/doctor/{doctorId}")
    @PreAuthorize("hasAnyRole('DOCTOR', 'ADMIN')")
    @Operation(summary = "Get appointments by doctor ID", description = "Retrieve appointments for specific doctor")
    public ResponseEntity<List<AppointmentResponse>> getAppointmentsByDoctorId(@PathVariable Long doctorId) {
        logger.info("Fetching appointments for doctor ID: {}", doctorId);
        return ResponseEntity.ok(appointmentService.getAppointmentsByDoctorId(doctorId));
    }

    @GetMapping("/patient/self")
    @PreAuthorize("hasRole('PATIENT')")
    @Operation(summary = "Get self appointments (patient)", description = "Get all appointments for logged-in patient")
    public ResponseEntity<List<AppointmentResponse>> getAppointmentsForLoggedInPatient() {
        logger.info("Fetching appointments for logged-in patient");
        return ResponseEntity.ok(appointmentService.getAppointmentsForLoggedInPatient());
    }

    @GetMapping("/doctor/self")
    @PreAuthorize("hasRole('DOCTOR')")
    @Operation(summary = "Get self appointments (doctor)", description = "Get all appointments for logged-in doctor")
    public ResponseEntity<List<AppointmentResponse>> getAppointmentsForLoggedInDoctor() {
        logger.info("Fetching appointments for logged-in doctor");
        return ResponseEntity.ok(appointmentService.getAppointmentsForLoggedInDoctor());
    }

    @GetMapping("/upcoming")
    @PreAuthorize("hasAnyRole('DOCTOR', 'ADMIN')")
    @Operation(summary = "Get upcoming appointments", description = "Retrieve upcoming appointments")
    public ResponseEntity<List<AppointmentResponse>> getUpcomingAppointments() {
        logger.info("Fetching upcoming appointments");
        return ResponseEntity.ok(appointmentService.getUpcomingAppointments());
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('DOCTOR')")
    @Operation(summary = "Update appointment", description = "Update appointment details")
    public ResponseEntity<AppointmentResponse> updateAppointment(
            @PathVariable Long id,
            @Valid @RequestBody AppointmentRequest request) {
        logger.info("Updating appointment with ID: {}", id);
        return ResponseEntity.ok(appointmentService.updateAppointment(id, request));
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasRole('DOCTOR')")
    @Operation(summary = "Update status", description = "Update appointment status")
    public ResponseEntity<AppointmentResponse> updateStatus(
            @PathVariable Long id,
            @RequestParam @Valid String status) {
        logger.info("Updating status for appointment ID: {} to {}", id, status);
        return ResponseEntity.ok(appointmentService.updateAppointmentStatus(id, status));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Delete appointment", description = "Delete an appointment")
    public ResponseEntity<Void> deleteAppointment(@PathVariable Long id) {
        logger.warn("Deleting appointment with ID: {}", id);
        appointmentService.deleteAppointment(id);
        return ResponseEntity.noContent().build();
    }
}
