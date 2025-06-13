package it.unipi.healthhub.controller.api;

import it.unipi.healthhub.dto.PasswordChangeDTO;
import it.unipi.healthhub.dto.PatientContactsDTO;
import it.unipi.healthhub.dto.UserDetailsDTO;
import it.unipi.healthhub.model.mongo.User;
import it.unipi.healthhub.service.UserService;
import it.unipi.healthhub.util.HashUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

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

    @GetMapping("/details/view")
    public ResponseEntity<PatientContactsDTO> getView(@RequestParam String id, HttpServletRequest request) {
        HttpSession session = request.getSession(false);

        if(id == null || id.isBlank()) {
            return ResponseEntity
                    .badRequest()
                    .build();
        }

        // Retrieve user from service
        Optional<User> OptUser = userService.getUserById(id);
        if(OptUser.isEmpty()){
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .build();
        }
        User user = OptUser.get();

        // Map User entity to PatientContactsDTO
        PatientContactsDTO dto = new PatientContactsDTO();
        dto.setName(user.getName());
        dto.setFiscalCode(user.getFiscalCode());
        dto.setBirthDate(user.getDob());
        dto.setGender(user.getGender());
        dto.setPhoneNumber(user.getPersonalNumber());
        dto.setEmail(user.getEmail());

        String doctorId = null;
        if (session != null) {
            Object attr = session.getAttribute("doctorId");
            if (attr instanceof String) {
                doctorId = (String) attr;
            }
        }

        if(doctorId != null && !doctorId.isBlank())
            dto.setNumberOfVisits(userService.getNumberOfVisitsByDoctor(doctorId, id));

        // Returns 200 OK with DTO
        return ResponseEntity
                .ok(dto);
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

    @GetMapping("/doctors/reviewed")
    public ResponseEntity<?> getReviewedDoctors(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        String patientId = (String) session.getAttribute("patientId");
        return ResponseEntity.ok(userService.getReviewedDoctors(patientId));
    }

    @GetMapping("/doctors/endorsed")
    public ResponseEntity<?> getEndorsedDoctors(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        String patientId = (String) session.getAttribute("patientId");
        return ResponseEntity.ok(userService.getEndorsedDoctors(patientId));
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
