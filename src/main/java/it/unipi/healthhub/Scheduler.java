package it.unipi.healthhub;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;

@Component
public class Scheduler {
    //@Scheduled(cron = "*/10 * * * * ?") // Every 10 seconds
    @Scheduled(cron = "0 0 0 ? * 7") // Every Sunday at midnight
    public void scheduleTask() {
        // Logic for scheduling tasks
        // We'll clean the schedules of the doctors
        // and add the new ones (the default ones)
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String currentTime = dateFormat.format(System.currentTimeMillis());
        System.out.println("Task scheduled at: " + currentTime);
    }
}
