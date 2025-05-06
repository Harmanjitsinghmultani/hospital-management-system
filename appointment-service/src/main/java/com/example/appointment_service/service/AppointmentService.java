package com.example.appointment_service.service;

import com.example.appointment_service.model.AppointmentRequest;
import com.example.appointment_service.model.AppointmentResponse;
import org.springframework.data.domain.Page;

import java.time.LocalDateTime;
import java.util.List;

public interface AppointmentService {
    AppointmentResponse createAppointment(AppointmentRequest appointmentRequest, String username);
    AppointmentResponse getAppointmentById(Long id);
    Page<AppointmentResponse> getAllAppointmentsPaginated(int page, int size, String sortBy, String direction);
    List<AppointmentResponse> getAllAppointments();
    List<AppointmentResponse> getAppointmentsByPatientId(Long patientId);
    List<AppointmentResponse> getAppointmentsByDoctorId(Long doctorId);
    List<AppointmentResponse> getAppointmentsForLoggedInPatient();
    List<AppointmentResponse> getAppointmentsForLoggedInDoctor();

    AppointmentResponse updateAppointment(Long id, AppointmentRequest appointmentRequest);
    void deleteAppointment(Long id);
    AppointmentResponse updateAppointmentStatus(Long id, String status);
    List<AppointmentResponse> getUpcomingAppointments();
    boolean isTimeSlotAvailable(Long doctorId, LocalDateTime appointmentTime);
}