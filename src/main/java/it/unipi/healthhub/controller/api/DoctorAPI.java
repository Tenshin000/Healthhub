package it.unipi.healthhub.controller.api;

import it.unipi.healthhub.model.*;
import it.unipi.healthhub.service.DoctorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/doctors")
public class DoctorAPI {

    @Autowired
    private DoctorService doctorService;

    // Metodo per la ricerca dei medici
    @GetMapping("/search")
    public ResponseEntity<List<Doctor>> search(@RequestParam(name = "query", required = false) String query) {
        List<Doctor> doctors = doctorService.searchDoctors(query);
        return ResponseEntity.ok(doctors);
    }

    @GetMapping
    public List<Doctor> getAllDoctors() {
        return doctorService.getAllDoctor();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Doctor> getDoctorById(@PathVariable String id) {
        return doctorService.getDoctorById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public Doctor createDoctor(@RequestBody Doctor doctor) {
        return doctorService.createDoctor(doctor);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Doctor> updateDoctor(@PathVariable String id, @RequestBody Doctor doctor) {
        Doctor updatedDoctor = doctorService.updateDoctor(id, doctor);
        if (updatedDoctor != null) {
            return ResponseEntity.ok(updatedDoctor);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDoctor(@PathVariable String id) {
        doctorService.deleteDoctor(id);
        return ResponseEntity.noContent().build();
    }

    // Endpoints for services
    @GetMapping("/{doctorId}/services")
    public ResponseEntity<List<Service>> getServices(@PathVariable String doctorId) {
        return ResponseEntity.ok(doctorService.getServices(doctorId));
    }

    @PostMapping("/{doctorId}/services")
    public ResponseEntity<Service> addService(@PathVariable String doctorId, @RequestBody Service service) {
        return ResponseEntity.ok(doctorService.addService(doctorId, service));
    }

    @PutMapping("/{doctorId}/services/{serviceId}")
    public ResponseEntity<Service> updateService(@PathVariable String doctorId, @PathVariable Integer serviceId, @RequestBody Service service) {
        return ResponseEntity.ok(doctorService.updateService(doctorId, serviceId, service));
    }

    @DeleteMapping("/{doctorId}/services/{serviceId}")
    public ResponseEntity<Void> deleteService(@PathVariable String doctorId, @PathVariable Integer serviceId) {
        doctorService.deleteService(doctorId, serviceId);
        return ResponseEntity.noContent().build();
    }

    // Endpoints for appointments
    @GetMapping("/{doctorId}/appointments")
    public ResponseEntity<List<Appointment>> getAppointments(@PathVariable String doctorId) {
        return ResponseEntity.ok(doctorService.getAppointments(doctorId));
    }

    @PostMapping("/{doctorId}/appointments")
    public ResponseEntity<Appointment> addAppointment(@PathVariable String doctorId, @RequestBody Appointment appointment) {
        return ResponseEntity.ok(doctorService.addAppointment(doctorId, appointment));
    }

    @DeleteMapping("/{doctorId}/appointments/{appointmentId}")
    public ResponseEntity<Void> deleteAppointment(@PathVariable String doctorId, @PathVariable String appointmentId) {
        doctorService.deleteAppointment(doctorId, appointmentId);
        return ResponseEntity.noContent().build();
    }

    // Endpoints for templates
    @GetMapping("/{doctorId}/templates")
    public ResponseEntity<List<CalendarTemplate>> getTemplates(@PathVariable String doctorId) {
        return ResponseEntity.ok(doctorService.getTemplates(doctorId));
    }

    @PostMapping("/{doctorId}/templates")
    public ResponseEntity<CalendarTemplate> addTemplate(@PathVariable String doctorId, @RequestBody CalendarTemplate template) {
        return ResponseEntity.ok(doctorService.addTemplate(doctorId, template));
    }

    @PutMapping("/{doctorId}/templates/{templateId}")
    public ResponseEntity<CalendarTemplate> updateTemplate(@PathVariable String doctorId, @PathVariable Integer templateId, @RequestBody CalendarTemplate template) {
        return ResponseEntity.ok(doctorService.updateTemplate(doctorId, templateId, template));
    }

    @DeleteMapping("/{doctorId}/templates/{templateId}")
    public ResponseEntity<Void> deleteTemplate(@PathVariable String doctorId, @PathVariable String templateId) {
        doctorService.deleteTemplate(doctorId, templateId);
        return ResponseEntity.noContent().build();
    }

    // Endpoints for calendars
    @GetMapping("/{doctorId}/calendars")
    public ResponseEntity<List<Schedule>> getCalendars(@PathVariable String doctorId) {
        return ResponseEntity.ok(doctorService.getCalendars(doctorId));
    }

    @PostMapping("/{doctorId}/calendars")
    public ResponseEntity<Schedule> addCalendar(@PathVariable String doctorId, @RequestBody Schedule calendar) {
        return ResponseEntity.ok(doctorService.addCalendar(doctorId, calendar));
    }

    @PutMapping("/{doctorId}/calendars/{calendarId}")
    public ResponseEntity<Schedule> updateCalendar(@PathVariable String doctorId, @PathVariable Integer calendarId, @RequestBody Schedule calendar) {
        return ResponseEntity.ok(doctorService.updateCalendar(doctorId, calendarId, calendar));
    }

    @DeleteMapping("/{doctorId}/calendars/{calendarId}")
    public ResponseEntity<Void> deleteCalendar(@PathVariable String doctorId, @PathVariable Integer calendarId) {
        doctorService.deleteCalendar(doctorId, calendarId);
        return ResponseEntity.noContent().build();
    }

    // Endpoints for reviews
    @GetMapping("/{doctorId}/reviews")
    public ResponseEntity<List<Review>> getReviews(@PathVariable String doctorId) {
        return ResponseEntity.ok(doctorService.getReviews(doctorId));
    }

    @PostMapping("/{doctorId}/reviews")
    public ResponseEntity<Review> addReview(@PathVariable String doctorId, @RequestBody Review review) {
        return ResponseEntity.ok(doctorService.addReview(doctorId, review));
    }

    @DeleteMapping("/{doctorId}/reviews/{reviewId}")
    public ResponseEntity<Void> deleteReview(@PathVariable String doctorId, @PathVariable Integer reviewId) {
        doctorService.deleteReview(doctorId, reviewId);
        return ResponseEntity.noContent().build();
    }
}
