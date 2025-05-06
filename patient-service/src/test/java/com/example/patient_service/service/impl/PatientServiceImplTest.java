package com.example.patient_service.service.impl;

import com.example.patient_service.model.Patient;
import com.example.patient_service.repository.PatientRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PatientServiceImplTest {

    @Mock
    private PatientRepository patientRepository;

    @InjectMocks
    private PatientServiceImpl patientService;

    private AutoCloseable closeable;

    @BeforeEach
    void setUp() {
        closeable = MockitoAnnotations.openMocks(this);
    }

    @Test
    void createPatient_ShouldReturnSavedPatient() {
        Patient input = new Patient();
        input.setName("John");
        input.setAge(25);

        Patient saved = new Patient();
        saved.setId(1L);
        saved.setName("John");
        saved.setAge(25);

        when(patientRepository.save(any(Patient.class))).thenReturn(saved);

        Patient result = patientService.createPatient(input);

        assertNotNull(result);
        assertEquals("John", result.getName());
        assertEquals(25, result.getAge());
        verify(patientRepository, times(1)).save(input);
    }

    @Test
    void getPatientById_ShouldReturnPatient_WhenExists() {
        Patient patient = new Patient();
        patient.setId(1L);
        patient.setName("Alice");

        when(patientRepository.findById(1L)).thenReturn(Optional.of(patient));

        Patient result = patientService.getPatientById(1L);

        assertNotNull(result);
        assertEquals("Alice", result.getName());
        verify(patientRepository, times(1)).findById(1L);
    }

    @Test
    void getPatientById_ShouldThrowException_WhenNotFound() {
        when(patientRepository.findById(99L)).thenReturn(Optional.empty());

        Exception exception = assertThrows(RuntimeException.class, () -> {
            patientService.getPatientById(99L);
        });

        assertTrue(exception.getMessage().contains("Patient not found"));
        verify(patientRepository, times(1)).findById(99L);
    }

    @Test
    void getAllPatients_ShouldReturnPatientList() {
        List<Patient> patients = List.of(
                new Patient() {{ setId(1L); setName("A"); }},
                new Patient() {{ setId(2L); setName("B"); }}
        );

        when(patientRepository.findAll()).thenReturn(patients);

        List<Patient> result = patientService.getAllPatients();

        assertEquals(2, result.size());
        verify(patientRepository, times(1)).findAll();
    }

    @Test
    void updatePatient_ShouldUpdateAndReturnPatient() {
        Patient existing = new Patient();
        existing.setId(1L);
        existing.setName("Old Name");
        existing.setAge(30);

        Patient updates = new Patient();
        updates.setName("New Name");
        updates.setAge(35);

        when(patientRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(patientRepository.save(any(Patient.class))).thenReturn(existing);

        Patient result = patientService.updatePatient(1L, updates);

        assertEquals("New Name", result.getName());
        assertEquals(35, result.getAge());
        verify(patientRepository).save(existing);
    }

    @Test
    void deletePatient_ShouldCallRepositoryDelete() {
        doNothing().when(patientRepository).deleteById(1L);

        patientService.deletePatient(1L);

        verify(patientRepository, times(1)).deleteById(1L);
    }
}
