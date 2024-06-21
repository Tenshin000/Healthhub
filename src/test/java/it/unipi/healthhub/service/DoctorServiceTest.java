package it.unipi.healthhub.service;

import it.unipi.healthhub.model.Doctor;
import it.unipi.healthhub.repository.DoctorRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class DoctorServiceTest {
    @Mock
    private DoctorRepository doctorRepository;

    @InjectMocks
    private DoctorService doctorService;

    private Doctor doc1;
    private Doctor doc2;

    @BeforeEach
    void setUp(){
        MockitoAnnotations.openMocks(this);
        doc1 = new Doctor();
        doc2 = new Doctor();
    }

    @Test
    void testGetAllDoctor(){
        when(doctorRepository.findAll()).thenReturn(Arrays.asList(doc1, doc2));

        List<Doctor> doctors = doctorService.getAllDoctor();
        assertEquals(2, doctors.size());
        verify(doctorRepository, times(1)).findAll();
    }
}
