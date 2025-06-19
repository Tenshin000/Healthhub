package it.unipi.healthhub.repository.mongo;

import it.unipi.healthhub.model.mongo.Doctor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Date;
import java.util.List;

@SpringBootTest
public class DoctorMongoRepositoryTest {

    @Autowired
    private DoctorMongoRepository doctorRepository;

    @Test
    void testFindDoctorsMissingSchedulesInNext4Weeks(){
        List<Doctor> doctors = doctorRepository.findDoctorsMissingSchedulesInNext4Weeks();
        System.out.println("Doctors missing schedules in next 4 weeks: " + doctors.size());
    }

    @Test
    void testFindSchedulesWithinNext4Weeks(){
        String doctorId = "682c9ea875b57f839e6fb927";
        List<Date> foundSchedules = doctorRepository.findSchedulesWithinNext4Weeks(doctorId);
        System.out.println("Found schedules in next 4 weeks for doctor " + doctorId + ": " + foundSchedules.size());
    }

    @Test
    void testCheckScheduleSlot() {
        String doctorId = "684adad437804916ca65ef04";
        Integer year = 2025;
        Integer week = 27;
        String keyDay = "monday";
        String slotStart = "08:00";
        boolean isSlotTaken = doctorRepository.checkScheduleSlot(doctorId, year, week, keyDay, slotStart);
        System.out.println("Is schedule slot taken for doctor " + doctorId + ": " + isSlotTaken);
    }
}
