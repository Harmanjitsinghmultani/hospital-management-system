package com.example.billing.service;

import com.example.billing.client.DoctorClient;
import com.example.billing.client.DoctorResponse;
import com.example.billing.client.PatientClient;
import com.example.billing.client.PatientResponse;
import com.example.billing.model.Bill;
import com.example.billing.repository.BillingRepository;
import com.example.billing.service.BillingServiceImpl; // ✅ correct
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class BillingServiceImplTest {

    @InjectMocks
    private BillingServiceImpl billingService;

    @Mock
    private BillingRepository billingRepository;

    @Mock
    private PatientClient patientClient;

    @Mock
    private DoctorClient doctorClient;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void saveBill_ShouldValidateAndSave() {
        Bill bill = new Bill(1L, null, 2L, null,
                BigDecimal.valueOf(500.00), LocalDateTime.now(), "PENDING", "General checkup");

        PatientResponse patient = new PatientResponse();
        patient.setId(1L);
        patient.setName("Alice");

        DoctorResponse doctor = new DoctorResponse();
        doctor.setId(2L);
        doctor.setName("Dr. Bob");

        when(patientClient.getPatientById(1L)).thenReturn(patient);
        when(doctorClient.getDoctorById(2L)).thenReturn(doctor);
        when(billingRepository.save(any(Bill.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Bill saved = billingService.saveBill(bill);

        assertEquals("Alice", saved.getPatientName());
        assertEquals("Dr. Bob", saved.getDoctorName());
        verify(billingRepository, times(1)).save(bill);
    }

    @Test
    void getAllBills_ShouldReturnList() {
        List<Bill> mockBills = Arrays.asList(
                new Bill(1L, "Alice", 2L, "Dr. Bob", BigDecimal.TEN, LocalDateTime.now(), "PAID", "desc"),
                new Bill(2L, "Bob", 3L, "Dr. Sam", BigDecimal.ONE, LocalDateTime.now(), "PENDING", "desc2")
        );

        when(billingRepository.findAll()).thenReturn(mockBills);

        List<Bill> result = billingService.getAllBills();
        assertEquals(2, result.size());
        verify(billingRepository).findAll();
    }

    @Test
    void getBillById_ShouldReturnBill() {
        Bill bill = new Bill(1L, "Alice", 2L, "Dr. Bob", BigDecimal.TEN, LocalDateTime.now(), "PAID", "desc");
        bill.setId(10L);

        when(billingRepository.findById(10L)).thenReturn(Optional.of(bill));

        Bill found = billingService.getBillById(10L);
        assertEquals("Alice", found.getPatientName());
    }

    @Test
    void getBillById_NotFound_ShouldThrow() {
        when(billingRepository.findById(99L)).thenReturn(Optional.empty());
        Exception ex = assertThrows(RuntimeException.class, () -> billingService.getBillById(99L));
        assertTrue(ex.getMessage().contains("Bill not found"));
    }

    @Test
    void getBillsByPatientId_ShouldReturnList() {
        List<Bill> mockList = Arrays.asList(new Bill(), new Bill());
        when(billingRepository.findByPatientId(1L)).thenReturn(mockList);
        List<Bill> result = billingService.getBillsByPatientId(1L);
        assertEquals(2, result.size());
    }

    @Test
    void getBillsByDoctorId_ShouldReturnList() {
        List<Bill> mockList = Arrays.asList(new Bill(), new Bill(), new Bill());
        when(billingRepository.findByDoctorId(2L)).thenReturn(mockList);
        List<Bill> result = billingService.getBillsByDoctorId(2L);
        assertEquals(3, result.size());
    }

    @Test
    void deleteBill_ShouldCallRepository() {
        billingService.deleteBill(55L);
        verify(billingRepository).deleteById(55L);
    }

    @Test
    void updateBillStatus_ShouldUpdate() {
        Bill bill = new Bill(1L, "Alice", 2L, "Dr. Bob", BigDecimal.TEN, LocalDateTime.now(), "PENDING", "desc");
        bill.setId(5L);

        when(billingRepository.findById(5L)).thenReturn(Optional.of(bill));
        when(billingRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        Bill updated = billingService.updateBillStatus(5L, "PAID");
        assertEquals("PAID", updated.getStatus());
    }

    @Test
    void updateBillStatus_BillNotFound_ShouldThrow() {
        when(billingRepository.findById(100L)).thenReturn(Optional.empty());
        Exception ex = assertThrows(RuntimeException.class, () -> billingService.updateBillStatus(100L, "PAID"));
        assertTrue(ex.getMessage().contains("Bill not found"));
    }
}
