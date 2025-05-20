package it.unipi.healthhub;

import it.unipi.healthhub.service.DoctorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;

@Component
public class Scheduler {
    @Autowired
    private DoctorService doctorService;
    @Scheduled(cron = "0 0 0 ? * 7") // Every Sunday at midnight
    public void scheduleTask() {
        doctorService.cleanOldSchedules();
        doctorService.setupNewSchedules();
    }
}
