package com.example.appointment_service.service.impl;

import com.example.appointment_service.client.DoctorClient;
import com.example.appointment_service.client.PatientClient;
import com.example.appointment_service.dto.Doctor;
import com.example.appointment_service.dto.Patient;
import com.example.appointment_service.model.Appointment;
import com.example.appointment_service.model.AppointmentRequest;
import com.example.appointment_service.model.AppointmentResponse;
import com.example.appointment_service.repository.AppointmentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AppointmentServiceImplTest {

    @InjectMocks
    private AppointmentServiceImpl appointmentService;

    @Mock
    private AppointmentRepository appointmentRepository;

    @Mock
    private DoctorClient doctorClient;

    @Mock
    private PatientClient patientClient;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void createAppointment_ShouldReturnResponse() {
        // Arrange
        AppointmentRequest request = new AppointmentRequest();
        request.setPatientId(1L);
        request.setDoctorId(2L);
        request.setAppointmentTime(LocalDateTime.of(2025, 5, 6, 10, 0));
        request.setReason("Checkup");
        request.setNotes("Bring reports");

        Doctor doctor = new Doctor();
        doctor.setId(2L);
        doctor.setName("Dr. A");

        Patient patient = new Patient();
        patient.setId(1L);
        patient.setName("Patient A");

        when(appointmentRepository.existsByDoctorIdAndAppointmentTime(eq(2L), any())).thenReturn(false);
        when(doctorClient.getDoctorById(2L)).thenReturn(doctor);
        when(patientClient.getPatientById(1L)).thenReturn(patient);
        when(appointmentRepository.save(any())).thenAnswer(invocation -> {
            Appointment saved = invocation.getArgument(0);
            saved.setId(1L); // simulate DB-generated ID
            return saved;
        });

        // Act
        AppointmentResponse response = appointmentService.createAppointment(request, "testuser");

        // Assert
        assertNotNull(response);
        assertEquals("Dr. A", response.getDoctorName());
        assertEquals("Patient A", response.getPatientName());
        assertEquals(2L, response.getDoctorId());
        assertEquals(1L, response.getPatientId());
    }

    @Test
    void getAppointmentById_ShouldReturnAppointment() {
        // Arrange
        LocalDateTime appointmentTime = LocalDateTime.of(2025, 5, 6, 10, 0);
        Appointment appointment = Appointment.builder()
                .id(1L)
                .patientId(1L)
                .doctorId(2L)
                .patientName("Patient A")
                .doctorName("Dr. A")
                .appointmentTime(appointmentTime)
                .reason("Checkup")
                .status("SCHEDULED")
                .build();

        when(appointmentRepository.findById(1L)).thenReturn(Optional.of(appointment));

        // Act
        AppointmentResponse result = appointmentService.getAppointmentById(1L);

        // Assert
        assertNotNull(result);
        assertEquals("Dr. A", result.getDoctorName());
        assertEquals("Patient A", result.getPatientName());
        assertEquals("SCHEDULED", result.getStatus());
    }

    @Test
    void getAllAppointmentsPaginated_ShouldReturnPage() {
        // Arrange
        Appointment appointment = Appointment.builder()
                .id(1L)
                .doctorName("Dr. A")
                .patientName("Patient A")
                .appointmentTime(LocalDateTime.of(2025, 5, 6, 10, 0))
                .build();

        PageRequest pageable = PageRequest.of(0, 5);
        Page<Appointment> mockedPage = new PageImpl<>(List.of(appointment));

        when(appointmentRepository.findAll(any(PageRequest.class))).thenReturn(mockedPage);

        // Act
        Page<AppointmentResponse> result = appointmentService.getAllAppointmentsPaginated(0, 5, "id", "asc");

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        assertEquals("Dr. A", result.getContent().get(0).getDoctorName());
        assertEquals("Patient A", result.getContent().get(0).getPatientName());
    }
}
