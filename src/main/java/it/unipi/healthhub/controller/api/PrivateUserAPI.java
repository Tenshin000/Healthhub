package it.unipi.healthhub.controller.api;

import it.unipi.healthhub.dto.PasswordChangeDTO;
import it.unipi.healthhub.dto.PatientContactsDTO;
import it.unipi.healthhub.dto.UserDetailsDTO;
import it.unipi.healthhub.service.UserService;
import it.unipi.healthhub.util.HashUtil;
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
        if (userService.cancelAppointment(appointmentId)) {
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.badRequest().body("Error: Appointment not found");
    }

    @GetMapping("/recommendations")
    public ResponseEntity<?> getRecommendedDoctors(HttpServletRequest request,
                                                   @RequestParam(defaultValue = "3") int limit){
        HttpSession session = request.getSession(false);
        String patientId = (String) session.getAttribute("patientId");
        return ResponseEntity.ok(userService.getRecommendedDoctors(patientId, limit));
    }

    @PutMapping("/password")
    public ResponseEntity<Void> changePassword(HttpServletRequest request, @RequestBody PasswordChangeDTO passwords){
        HttpSession session = request.getSession(false);
        String patientId = (String) session.getAttribute("patientId");
        passwords.setCurrentPassword(HashUtil.hashPassword(passwords.getCurrentPassword()));
        passwords.setNewPassword(HashUtil.hashPassword(passwords.getNewPassword()));

        boolean ok = userService.changePassword(patientId, passwords.getCurrentPassword(), passwords.getNewPassword());
        if(ok){
            return ResponseEntity.noContent().build();
        }
        else{
            return ResponseEntity.status(400).build();
        }
    }
}
