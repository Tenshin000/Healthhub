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
        String doctorId = "6818f7806767389ce1b13f68";
        List<Date> foundedSchedules = doctorRepository.findSchedulesWithinNext4Weeks(doctorId);
        System.out.println("Founded schedules in next 4 weeks for doctor " + doctorId + ": " + foundedSchedules.size());
    }
}
