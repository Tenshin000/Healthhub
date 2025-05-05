package it.unipi.healthhub.controller.api;

import it.unipi.healthhub.dto.PatientContactsDTO;
import it.unipi.healthhub.dto.UserDetailsDTO;
import it.unipi.healthhub.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
public class PrivateUserAPI {
    @Autowired
    private UserService userService;

    @GetMapping("/details")
    public ResponseEntity<?> getMyDetails(HttpServletRequest request) {
        try {
            HttpSession session = request.getSession(false);
            String patientId = (String) session.getAttribute("patientId");
            return ResponseEntity.ok(userService.getUserDetails(patientId));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }

    @PutMapping("/details")
    public ResponseEntity<?> updateMyDetails(@RequestBody UserDetailsDTO userDetails, HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        String patientId = (String) session.getAttribute("patientId");
        return ResponseEntity.ok(userService.updateUserDetails(patientId, userDetails));
    }

    @GetMapping("/contacts")
    public ResponseEntity<?> getMyContacts(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        String patientId = (String) session.getAttribute("patientId");
        return ResponseEntity.ok(userService.getUserContacts(patientId));
    }

    @PutMapping("/contacts")
    public ResponseEntity<?> updateMyContacts(@RequestBody PatientContactsDTO userContacts, HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        String patientId = (String) session.getAttribute("patientId");
        return ResponseEntity.ok(userService.updateUserContacts(patientId, userContacts));
    }

    @GetMapping("/appointments/upcoming")
    public ResponseEntity<?> getMyUpcomingAppointments(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        String patientId = (String) session.getAttribute("patientId");
        return ResponseEntity.ok(userService.getUpcomingAppointments(patientId));
    }

    @GetMapping("/appointments/past")
    public ResponseEntity<?> getMyPastAppointments(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        String patientId = (String) session.getAttribute("patientId");
        return ResponseEntity.ok(userService.getPastAppointments(patientId));
    }

    @DeleteMapping("/appointments/{appointmentId}")
    public ResponseEntity<?> deleteAppointment(@PathVariable String appointmentId) {
        if (userService.deleteAppointment(appointmentId)) {
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.badRequest().body("Error: Appointment not found");
    }

    @GetMapping("/recommendations")
    public ResponseEntity<?> getRecommendedDoctors(HttpServletRequest request,
                                                   @RequestParam(defaultValue = "2") int limit1,
                                                   @RequestParam(defaultValue = "2") int limit2){
        HttpSession session = request.getSession(false);
        String patientId = (String) session.getAttribute("patientId");
        return ResponseEntity.ok(userService.getRecommendedDoctors(patientId, limit1, limit2));
    }
}
