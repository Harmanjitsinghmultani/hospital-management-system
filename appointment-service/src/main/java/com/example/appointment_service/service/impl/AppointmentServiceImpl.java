package com.example.appointment_service.service.impl;

import com.example.appointment_service.client.DoctorClient;
import com.example.appointment_service.client.PatientClient;
import com.example.appointment_service.dto.Doctor;
import com.example.appointment_service.dto.Patient;
import com.example.appointment_service.exception.ResourceNotFoundException;
import com.example.appointment_service.model.Appointment;
import com.example.appointment_service.model.AppointmentRequest;
import com.example.appointment_service.model.AppointmentResponse;
import com.example.appointment_service.repository.AppointmentRepository;
import com.example.appointment_service.security.JwtService;
import com.example.appointment_service.service.AppointmentService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AppointmentServiceImpl implements AppointmentService {

    private static final Logger logger = LoggerFactory.getLogger(AppointmentServiceImpl.class);

    private final AppointmentRepository appointmentRepository;
    private final PatientClient patientClient;
    private final DoctorClient doctorClient;
    private final JwtService jwtService;

    @Override
    @Transactional
    public AppointmentResponse createAppointment(AppointmentRequest appointmentRequest, String username) {
        logger.debug("Creating appointment for patient: {}, doctor: {}, time: {}",
                appointmentRequest.getPatientId(),
                appointmentRequest.getDoctorId(),
                appointmentRequest.getAppointmentTime());

        if (!isTimeSlotAvailable(appointmentRequest.getDoctorId(), appointmentRequest.getAppointmentTime())) {
            logger.error("Time slot not available for doctor {} at {}",
                    appointmentRequest.getDoctorId(),
                    appointmentRequest.getAppointmentTime());
            throw new IllegalArgumentException("The selected time slot is not available");
        }

        Patient patient = patientClient.getPatientById(appointmentRequest.getPatientId());
        Doctor doctor = doctorClient.getDoctorById(appointmentRequest.getDoctorId());

        Appointment appointment = Appointment.builder()
                .patientId(patient.getId())
                .doctorId(doctor.getId())
                .patientName(patient.getName())
                .doctorName(doctor.getName())
                .appointmentDate(appointmentRequest.getAppointmentDate())
                .appointmentTime(appointmentRequest.getAppointmentTime())
                .reason(appointmentRequest.getReason())
                .status("SCHEDULED")
                .notes(appointmentRequest.getNotes())
                .createdBy(username)
                .build();

        Appointment savedAppointment = appointmentRepository.save(appointment);
        logger.info("Appointment created successfully with ID: {}", savedAppointment.getId());
        return mapToResponse(savedAppointment);
    }

    @Override
    @Transactional(readOnly = true)
    public AppointmentResponse getAppointmentById(Long id) {
        logger.debug("Fetching appointment with ID: {}", id);
        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> {
                    logger.error("Appointment not found with ID: {}", id);
                    return new ResourceNotFoundException("Appointment not found with id: " + id);
                });
        return mapToResponse(appointment);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<AppointmentResponse> getAllAppointmentsPaginated(int page, int size, String sortBy, String direction) {
        logger.debug("Fetching paginated appointments - page: {}, size: {}, sortBy: {}, direction: {}",
                page, size, sortBy, direction);

        Sort.Direction sortDirection = direction.equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sortBy));

        Page<Appointment> appointmentsPage = appointmentRepository.findAll(pageable);
        List<AppointmentResponse> responses = appointmentsPage.getContent()
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());

        return new PageImpl<>(responses, pageable, appointmentsPage.getTotalElements());
    }

    @Override
    @Transactional(readOnly = true)
    public List<AppointmentResponse> getAllAppointments() {
        logger.debug("Fetching all appointments");
        return appointmentRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<AppointmentResponse> getAppointmentsByPatientId(Long patientId) {
        logger.debug("Fetching appointments for patient ID: {}", patientId);
        return appointmentRepository.findByPatientId(patientId)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<AppointmentResponse> getAppointmentsByDoctorId(Long doctorId) {
        logger.debug("Fetching appointments for doctor ID: {}", doctorId);
        return appointmentRepository.findByDoctorId(doctorId)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<AppointmentResponse> getAppointmentsForLoggedInPatient() {
        Long userId = jwtService.extractUserIdFromCurrentToken();
        logger.debug("Fetching self appointments for patient ID: {}", userId);
        return getAppointmentsByPatientId(userId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AppointmentResponse> getAppointmentsForLoggedInDoctor() {
        Long userId = jwtService.extractUserIdFromCurrentToken();
        Doctor doctor = doctorClient.getDoctorByUserId(userId);
        if (doctor == null) {
            logger.error("Doctor not found for user ID: {}", userId);
            throw new ResourceNotFoundException("Doctor not found for user ID: " + userId);
        }
        logger.debug("Fetching self appointments for doctor ID: {}", doctor.getId());
        return getAppointmentsByDoctorId(doctor.getId());
    }

    @Override
    @Transactional
    public AppointmentResponse updateAppointment(Long id, AppointmentRequest appointmentRequest) {
        logger.debug("Updating appointment with ID: {}", id);
        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Appointment not found with id: " + id));

        if (!appointment.getAppointmentTime().equals(appointmentRequest.getAppointmentTime())) {
            if (!isTimeSlotAvailable(appointmentRequest.getDoctorId(), appointmentRequest.getAppointmentTime())) {
                throw new IllegalArgumentException("The selected time slot is not available");
            }
        }

        Patient patient = patientClient.getPatientById(appointmentRequest.getPatientId());
        Doctor doctor = doctorClient.getDoctorById(appointmentRequest.getDoctorId());

        appointment.setPatientId(patient.getId());
        appointment.setDoctorId(doctor.getId());
        appointment.setPatientName(patient.getName());
        appointment.setDoctorName(doctor.getName());
        appointment.setAppointmentDate(appointmentRequest.getAppointmentDate());
        appointment.setAppointmentTime(appointmentRequest.getAppointmentTime());
        appointment.setReason(appointmentRequest.getReason());
        appointment.setNotes(appointmentRequest.getNotes());

        Appointment updatedAppointment = appointmentRepository.save(appointment);
        logger.info("Appointment updated successfully with ID: {}", id);
        return mapToResponse(updatedAppointment);
    }

    @Override
    @Transactional
    public void deleteAppointment(Long id) {
        logger.debug("Deleting appointment with ID: {}", id);
        if (!appointmentRepository.existsById(id)) {
            throw new ResourceNotFoundException("Appointment not found with id: " + id);
        }
        appointmentRepository.deleteById(id);
        logger.info("Appointment deleted successfully with ID: {}", id);
    }

    @Override
    @Transactional
    public AppointmentResponse updateAppointmentStatus(Long id, String status) {
        logger.debug("Updating status for appointment ID: {} to {}", id, status);
        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Appointment not found with id: " + id));
        appointment.setStatus(status);
        Appointment updatedAppointment = appointmentRepository.save(appointment);
        return mapToResponse(updatedAppointment);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AppointmentResponse> getUpcomingAppointments() {
        LocalDateTime now = LocalDateTime.now();
        logger.debug("Fetching upcoming appointments after {}", now);
        return appointmentRepository.findByAppointmentTimeAfter(now)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isTimeSlotAvailable(Long doctorId, LocalDateTime appointmentTime) {
        logger.debug("Checking time slot availability for doctor {} at {}", doctorId, appointmentTime);
        return !appointmentRepository.existsByDoctorIdAndAppointmentTime(doctorId, appointmentTime);
    }

    private AppointmentResponse mapToResponse(Appointment appointment) {
        return AppointmentResponse.builder()
                .id(appointment.getId())
                .patientId(appointment.getPatientId())
                .doctorId(appointment.getDoctorId())
                .patientName(appointment.getPatientName())
                .doctorName(appointment.getDoctorName())
                .appointmentDate(appointment.getAppointmentDate())
                .appointmentTime(appointment.getAppointmentTime())
                .reason(appointment.getReason())
                .status(appointment.getStatus())
                .notes(appointment.getNotes())
                .createdAt(appointment.getCreatedAt())
                .build();
    }
}
