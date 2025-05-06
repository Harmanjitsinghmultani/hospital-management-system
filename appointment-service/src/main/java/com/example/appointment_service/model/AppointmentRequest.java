package com.example.appointment_service.model;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AppointmentRequest {

    @NotNull(message = "Patient ID is required")
    private Long patientId;

    @NotNull(message = "Doctor ID is required")
    private Long doctorId;

    @NotNull(message = "Appointment Date is required")
    @Future(message = "Appointment Date must be in the future")
    private LocalDateTime appointmentDate;

    @NotNull(message = "Appointment Time is required")
    @Future(message = "Appointment Time must be in the future")
    private LocalDateTime appointmentTime;

    @NotNull(message = "Reason is required")
    private String reason;

    private String notes; // optional notes (for extra comments if needed)
}
