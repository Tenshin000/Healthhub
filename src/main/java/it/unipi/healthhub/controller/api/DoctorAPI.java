package it.unipi.healthhub.controller.api;

import it.unipi.healthhub.model.mongo.*;
import it.unipi.healthhub.dto.*;
import it.unipi.healthhub.service.AppointmentService;
import it.unipi.healthhub.service.DoctorService;

import it.unipi.healthhub.util.DateUtil;
import it.unipi.healthhub.service.UserService;
import it.unipi.healthhub.util.ScheduleConverter;
import it.unipi.healthhub.util.TemplateConverter;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.util.Pair;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/doctors")
public class DoctorAPI {
    @Autowired
    private DoctorService doctorService;

    @Autowired
    private UserService userService;

    @Autowired
    private AppointmentService appointmentService;
/*
    // Metodo per la ricerca dei medici
    @GetMapping("/search")
    public ResponseEntity<List<Doctor>> search(@RequestParam(name = "query", required = false) String query) {
        List<Doctor> doctors = doctorService.searchDoctorsMongo(query);
        return ResponseEntity.ok(doctors);
    }
*/
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

    // Endpoints for appointments
    @GetMapping("/{doctorId}/appointments")
    public ResponseEntity<List<Appointment>> getAppointments(@PathVariable String doctorId) {
        return ResponseEntity.ok(doctorService.getAppointments(doctorId));
    }

    @PostMapping("/{doctorId}/appointments")
    public ResponseEntity<AppointmentDTO> bookAnAppointment(
            @PathVariable String doctorId,
            @RequestBody AppointmentDTO appointmentDto,
            HttpServletRequest request) {

        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("patientId") == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build(); // HTTP 401 Unauthorized
        }

        String patientId = (String) session.getAttribute("patientId");

        boolean booked = doctorService.bookAnAppointment(doctorId, appointmentDto, patientId);
        if (booked) {
            return ResponseEntity.ok(appointmentDto);
        } else {
            return ResponseEntity.badRequest().build(); // HTTP 400 Bad Request
        }
    }

    // Endpoints for templates
    @GetMapping("/{doctorId}/templates")
    public ResponseEntity<List<CalendarTemplate>> getTemplates(@PathVariable String doctorId) {
        return ResponseEntity.ok(doctorService.getTemplates(doctorId));
    }

    // Endpoints for schedules
    @GetMapping("/{doctorId}/schedules/week")
    public ResponseEntity<ScheduleDTO> getSchedule(@PathVariable String doctorId, @RequestParam Integer year, @RequestParam Integer week) {
        Pair<Schedule, Integer> response = doctorService.getSchedule(doctorId, year, week);
        if (response != null) {
            Schedule schedule = (Schedule) response.getFirst();
            Map<String, List<PrenotableSlot>> modelSlots = schedule.getSlots();
            Map<String, List<ScheduleDTO.PrenotableSlotDTO>> dtoSlots = ScheduleConverter.convertToDtoSlots(modelSlots);
            ScheduleDTO scheduleDto = new ScheduleDTO(schedule.getWeek(), dtoSlots);
            return ResponseEntity.ok(scheduleDto);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            //return ResponseEntity.notFound().build();
        }
    }

    // Endpoints for endorse
    @GetMapping("/{doctorId}/endorsements")
    public ResponseEntity<EndorsementDTO> getEndorsements(@PathVariable String doctorId, HttpServletRequest request) {
        Integer endorsements = doctorService.getEndorsements(doctorId);
        HttpSession session = request.getSession(false);
        boolean hasEndorsed = false;
        if (session != null) {
            String patientId = (String) session.getAttribute("patientId");
            if (patientId != null) {
                hasEndorsed = userService.hasEndorsed(patientId, doctorId);
            }
        }

        if (endorsements != null) {
            EndorsementDTO endorsementDto = new EndorsementDTO(endorsements, hasEndorsed);
            return ResponseEntity.ok(endorsementDto);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/{doctorId}/endorse")
    public ResponseEntity<EndorsementDTO> endorseDoctor(@PathVariable String doctorId, HttpServletRequest request) {
        HttpSession session = request.getSession(false);

        if(session == null)
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

        String patientId = (String) session.getAttribute("patientId");
        doctorService.endorse(doctorId, patientId);
        Integer endorsementCount = doctorService.getEndorsements(doctorId);
        EndorsementDTO endorsementDto = new EndorsementDTO(endorsementCount, true);
        return ResponseEntity.ok(endorsementDto);
    }

    @PostMapping("/{doctorId}/unendorse")
    public ResponseEntity<EndorsementDTO> unendorseDoctor(@PathVariable String doctorId, HttpServletRequest request) {
        HttpSession session = request.getSession(false);

        if(session == null)
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

        String patientId = (String) session.getAttribute("patientId");
        doctorService.unendorse(doctorId, patientId);
        Integer endorsementCount = doctorService.getEndorsements(doctorId);
        EndorsementDTO endorsementDto = new EndorsementDTO(endorsementCount, false);
        return ResponseEntity.ok(endorsementDto);
    }

    // Endpoints for reviews
    @GetMapping("/{doctorId}/reviews")
    public ResponseEntity<List<Review>> getReviews(@PathVariable String doctorId) {
        List<Review> reviews = doctorService.getReviews(doctorId);
        if (reviews != null) {
            return ResponseEntity.ok(reviews);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/{doctorId}/reviews")
    public ResponseEntity<Review> addReview(@PathVariable String doctorId, @RequestBody ReviewDTO review, HttpServletRequest request) {
        // Set the current date and the name of the user
        HttpSession session = request.getSession(false);
        LocalDate currentDate = LocalDate.now();
        review.setDate(currentDate);
        String patientId = (String) session.getAttribute("patientId");
        Optional<User> userOpt = userService.getUserById(patientId);
        if(userOpt.isPresent()){
            review.setName(userOpt.get().getName());

            // Check beforehand if the doctor id matches an existing doctor
            Optional<Doctor> doctorOpt = doctorService.getDoctorById(doctorId);
            if(doctorOpt.isPresent()){
                // This way I already know that if it returns null it is because the patient has never been seen by the doctor and therefore cannot review him
                Review newReview = doctorService.addReview(doctorId, patientId, review);
                if(newReview != null){
                    return ResponseEntity.ok(newReview);
                }
                else{
                    return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
                }
            }
            else{
                return ResponseEntity.badRequest().build();
            }
        }
        else{
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/{doctorId}/reviews/week/count")
    public ResponseEntity<Integer> getReviewsCount(@PathVariable String doctorId){
        List<Review> reviews = doctorService.getReviews(doctorId);

        if(reviews != null){
            LocalDate currentDate = LocalDate.now();

            // Filter reviews that are in the same week as the currentDate
            // Counting the reviews that are made in the same week of now
            long count = reviews.stream()
                    .filter(review -> DateUtil.isSameWeek(review.getDate(), currentDate))
                    .count();

            return ResponseEntity.ok((int) count);
        }
        else{
            return ResponseEntity.notFound().build();
        }
    }
}
