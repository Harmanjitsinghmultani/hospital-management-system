package com.hospital.doctor_service.service.impl;

import com.hospital.doctor_service.model.Doctor;
import com.hospital.doctor_service.repository.DoctorRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class DoctorServiceImplTest {

    @InjectMocks
    private DoctorServiceImpl doctorService;

    @Mock
    private DoctorRepository doctorRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void saveDoctor_ShouldReturnSavedDoctor() {
        Doctor doctor = new Doctor("Dr. John", "Cardiology", "john@example.com", "1234567890", 101L);
        doctor.setId(1L);

        when(doctorRepository.save(any(Doctor.class))).thenReturn(doctor);

        Doctor result = doctorService.saveDoctor(doctor);

        assertNotNull(result);
        assertEquals("Dr. John", result.getName());
        verify(doctorRepository, times(1)).save(doctor);
    }

    @Test
    void getAllDoctors_ShouldReturnList() {
        List<Doctor> mockDoctors = Arrays.asList(
                new Doctor("Dr. A", "Neuro", "a@example.com", "1111111111", 1L),
                new Doctor("Dr. B", "Ortho", "b@example.com", "2222222222", 2L)
        );

        when(doctorRepository.findAll()).thenReturn(mockDoctors);

        List<Doctor> result = doctorService.getAllDoctors();

        assertEquals(2, result.size());
        verify(doctorRepository, times(1)).findAll();
    }

    @Test
    void getDoctorById_ShouldReturnDoctor() {
        Doctor doctor = new Doctor("Dr. C", "Dermatology", "c@example.com", "3333333333", 3L);
        doctor.setId(5L);

        when(doctorRepository.findById(5L)).thenReturn(Optional.of(doctor));

        Doctor result = doctorService.getDoctorById(5L);

        assertNotNull(result);
        assertEquals("Dr. C", result.getName());
    }

    @Test
    void getDoctorById_NotFound_ShouldThrowException() {
        when(doctorRepository.findById(10L)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                doctorService.getDoctorById(10L));

        assertEquals("Doctor not found with ID: 10", exception.getMessage());
    }

    @Test
    void getDoctorByUserId_ShouldReturnDoctor() {
        Doctor doctor = new Doctor("Dr. D", "Pediatrics", "d@example.com", "4444444444", 99L);

        when(doctorRepository.findByUserId(99L)).thenReturn(Optional.of(doctor));

        Doctor result = doctorService.getDoctorByUserId(99L);

        assertNotNull(result);
        assertEquals("Dr. D", result.getName());
    }

    @Test
    void getDoctorByUserId_NotFound_ShouldThrowException() {
        when(doctorRepository.findByUserId(999L)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                doctorService.getDoctorByUserId(999L));

        assertEquals("Doctor not found with userId: 999", exception.getMessage());
    }

    @Test
    void deleteDoctor_ShouldInvokeRepositoryDelete() {
        doctorService.deleteDoctor(7L);
        verify(doctorRepository, times(1)).deleteById(7L);
    }
}
