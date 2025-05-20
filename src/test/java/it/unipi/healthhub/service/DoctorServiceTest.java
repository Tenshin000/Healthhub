package it.unipi.healthhub.service;

import it.unipi.healthhub.model.mongo.CalendarTemplate;
import it.unipi.healthhub.model.mongo.Doctor;
import it.unipi.healthhub.repository.mongo.DoctorMongoRepository;
import it.unipi.healthhub.util.DateUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@SpringBootTest
public class DoctorServiceTest {
    @Autowired
    private DoctorMongoRepository doctorRepository;

    @Autowired
    private DoctorService doctorService;

    private Doctor doc1;
    private Doctor doc2;

    @BeforeEach
    void setUp(){
    }

    @Test
    void testFindDoctorById(){
        Optional<Doctor> doctorOpt = doctorRepository.findById("682c937d1afbbba495e9a5f7");
        if(doctorOpt.isPresent()){
            Doctor doctor = doctorOpt.get();
            System.out.println("Doctor found: " + doctor);
        } else {
            System.out.println("Doctor not found");
        }
    }

    @Test
    void testGetAllDoctor(){
    }

    @Test
    void testGetDefaultTemplate(){
        String doctorId = "682c9ea875b57f839e6fb927";
        CalendarTemplate defaultTemplate = doctorService.getDefaultTemplate(doctorId);

        System.out.println("Default template for doctor " + doctorId + ": " + defaultTemplate);
    }

    @Test
    void testFindDates(){
        List<Date> foundSchedules = doctorRepository.findSchedulesWithinNext4Weeks("682c9ea875b57f839e6fb927");

        Set<LocalDate> foundDates = foundSchedules.stream()
                .map(DateUtil::convertToLocalDate)
                .collect(Collectors.toSet());

        System.out.println("Founded dates in next 4 weeks: " + foundDates.size());
        System.out.println("Founded dates in next 4 weeks: " + foundDates);
    }

    @Test
    void testAllMonday(){
        LocalDate start = LocalDate.now();
        List<LocalDate> allMondays = DateUtil.getNext4Mondays(start);
        System.out.println("All Mondays in the next 4 weeks: " + allMondays.size());
        System.out.println("All Mondays in the next 4 weeks: " + allMondays);
    }

    @Test
    void testFindDoctorsMissingSchedulesInNext4Weeks(){
        List<Doctor> doctors = doctorRepository.findDoctorsMissingSchedulesInNext4Weeks();
        System.out.println("Doctors missing schedules in next 4 weeks: " + doctors.size());
    }

    @Test
    void testSetDifference(){
        List<LocalDate> allMondays = DateUtil.getNext4Mondays(LocalDate.now());
        List<Date> foundSchedules = doctorRepository.findSchedulesWithinNext4Weeks("682c9ea875b57f839e6fb927");

        Set<LocalDate> foundDates = foundSchedules.stream()
                .map(DateUtil::convertToLocalDate)
                .collect(Collectors.toSet());

        List<LocalDate> missingDates = allMondays.stream()
                .filter(date -> !foundDates.contains(date))
                .toList();

        System.out.println("All Mondays in the next 4 weeks: " + allMondays.size());
        System.out.println("All Mondays in the next 4 weeks: " + allMondays);

        System.out.println("Founded dates in next 4 weeks: " + foundDates.size());
        System.out.println("Founded dates in next 4 weeks: " + foundDates);

        System.out.println("Missing dates: " + missingDates.size());
        System.out.println("Missing dates: " + missingDates);
    }

    @Test
    void testSetupNewSchedules(){
        doctorService.setupNewSchedules();
    }

    @Test
    void testCleanOldSchedules(){
        // Get next 4 Mondays, then acceess the 2nd
        List<LocalDate> allMondays = DateUtil.getNext4Mondays(LocalDate.now());
        LocalDate targetMonday = allMondays.get(3);
        doctorService.cleanOldSchedules(targetMonday);
    }
}
