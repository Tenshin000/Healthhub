package it.unipi.healthhub.controller;

import it.unipi.healthhub.exception.UserAlreadyExistsException;
import it.unipi.healthhub.model.mongo.Address;
import it.unipi.healthhub.model.mongo.Doctor;
import it.unipi.healthhub.model.mongo.User;
import it.unipi.healthhub.service.DoctorService;
import it.unipi.healthhub.service.UserService;
import it.unipi.healthhub.util.ControllerUtil;
import it.unipi.healthhub.util.HashUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Controller
public class AuthController {

    @Autowired
    private DoctorService doctorService;

    @Autowired
    private UserService userService;

    // Display login page
    @GetMapping("/login")
    public String loginForm(Model model, HttpServletRequest request) {
        ControllerUtil.setSessionModel(model, request);
        return "login";
    }

    // Handle login form submission
    @PostMapping("/login")
    public String login(HttpServletRequest request, Model model) {
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        String hashPassword = HashUtil.hashPassword(password);

        HttpSession session = request.getSession();

        // Check if the user is a patient
        User user = userService.loginUser(username, hashPassword);
        if (user != null) {
            // Set session attributes for the patient
            session.setAttribute("username", user.getUsername());
            session.setAttribute("patientId", user.getId());
            session.setAttribute("role", "patient");
            return "redirect:/index"; // Redirect to home page after login
        }

        // Check if the user is a doctor
        Doctor doctor = doctorService.loginDoctor(username, hashPassword);
        if (doctor != null) {
            // Set session attributes for the doctor
            session.setAttribute("username", doctor.getUsername());
            session.setAttribute("doctorId", doctor.getId());
            session.setAttribute("role", "doctor");
            return "redirect:/doctor/dashboard"; // Redirect to doctor's dashboard after login
        }

        // If neither patient nor doctor is found, show an error message
        session.invalidate();
        model.addAttribute("error", "Credenziali non valide");
        return "login";
    }

    // Handle logout and invalidate session
    @GetMapping("/logout")
    public String logout(HttpServletRequest request) {
        request.getSession().invalidate();
        return "redirect:/login";
    }

    // Display patient registration form
    @GetMapping("/register")
    public String registerPage(Model model, HttpServletRequest request) {
        ControllerUtil.setSessionModel(model, request);
        model.addAttribute("user", new User());
        return "register";
    }

    // Handle patient registration
    @PostMapping("/register")
    public String registerUser(HttpServletRequest request, @ModelAttribute User user, Model model) {
        User controlUser = null;
        try {
            String password = HashUtil.hashPassword(user.getPassword());
            user.setPassword(password);
            String phone = request.getParameter("phone");
            user.setPersonalNumber(phone);
            controlUser = userService.createUser(user);
            if (controlUser == null)
                throw new UserAlreadyExistsException();

            System.out.println("We");
            return "redirect:/login";
        } catch (UserAlreadyExistsException e) {
            System.out.println("GG");
            // If registration fails, return an error message to the view
            model.addAttribute("logged", false);
            model.addAttribute("errorMessage", "Registration failed: " + e.getMessage());
            return "register"; // name of the Thymeleaf template
        }
    }

    // Display doctor registration form
    @GetMapping("/register-doctor")
    public String registerPageDoctor(Model model, HttpServletRequest request) {
        ControllerUtil.setSessionModel(model, request);
        model.addAttribute("doctor", new Doctor());
        return "register-doctor";
    }

    // Handle doctor registration
    @PostMapping("/register-doctor")
    public String registerDoctor(HttpServletRequest request, @ModelAttribute Doctor doctor, Model model) {
        Doctor controlDoctor = null;

        try {
            // Get the specialization string from the form (e.g. "Cardiology, Pediatrics, Neurology")
            String specializationString = request.getParameter("specialization");

            if (specializationString != null && !specializationString.isBlank()) {
                // Split the string on commas and remove extra spaces
                List<String> specializationList = List.of(specializationString.split(","))
                        .stream()
                        .map(String::trim)
                        .filter(s -> !s.isEmpty())
                        .toList();

                doctor.setSpecializations(specializationList);
            }

            // Extract the phone number string from the form input
            String phoneNumber = request.getParameter("phone");

            if (phoneNumber != null && !phoneNumber.isBlank()) {
                // Add the single phone number as a one-element list
                List<String> phoneList = new ArrayList<>();
                phoneList.add(phoneNumber.trim());

                // Set the phone number list to the doctor object
                doctor.setPhoneNumbers(phoneList);
            }

            doctor.setAddress(createAddress(request));

            String password = HashUtil.hashPassword(doctor.getPassword());
            doctor.setPassword(password);

            controlDoctor = doctorService.createDoctor(doctor);
            if (controlDoctor == null)
                throw new UserAlreadyExistsException();
            return "redirect:/login";
        } catch (UserAlreadyExistsException e) {
            // If registration fails, return an error message to the view
            model.addAttribute("logged", false);
            model.addAttribute("errorMessage", "Doctor registration failed: " + e.getMessage());
            return "register-doctor"; // name of the Thymeleaf template
        }
    }

    // Display forgot password page
    @GetMapping("/forgot-password")
    public String forgotPasswordForm(Model model, HttpServletRequest request) {
        ControllerUtil.setSessionModel(model, request);
        return "forgot-password";
    }

    // Handle forgot‑password submission
    @PostMapping("/forgot-password")
    public ResponseEntity<Boolean> forgotPasswordFormSubmit(HttpServletRequest request, Model model) {
        String email = request.getParameter("email");
        ControllerUtil.setSessionModel(model, request);

        // Lookup patient account by email
        User user = userService.findByEmail(email);
        boolean isDoctor = false;

        if (user == null) {
            // If not a patient, try doctor
            Doctor doctor = doctorService.findByEmail(email);
            if (doctor != null) {
                user = doctor;
                isDoctor = true; // treat doctor as “account”
            }
        }

        if (user == null) {
            // No account found
            model.addAttribute("error", "No account found for that email address.");
            return ResponseEntity.ok(false);
        }

        // Build reset link: e.g. https://your-domain/forgot-password/{id}
        String appUrl = request.getScheme() + "://"
                + request.getServerName()
                + (request.getServerPort() != 80 ? ":" + request.getServerPort() : "")
                + request.getContextPath();
        String resetLink = appUrl + "/forgot-password/" + user.getId();

        boolean mailSent = false;

        // Send the email
        if(isDoctor){
            mailSent = doctorService.sendPasswordReset(user.getEmail(), resetLink);
        }
        else{
            mailSent = userService.sendPasswordReset(user.getEmail(), resetLink);
        }

        if (mailSent) {
            model.addAttribute("message",
                    "A password reset link has been sent to your email address.");
            return ResponseEntity.ok(true);
        } else {
            model.addAttribute("error",
                    "Failed to send reset link. Please try again later.");
            return ResponseEntity.ok(false);
        }
    }

    @GetMapping("/forgot-password/{id}")
    public String showResetForm(@PathVariable String id, Model model) {
        // Eventually check that the id exists and token expiration…
        model.addAttribute("userId", id);
        return "forgot-password-reset";
    }

    // Manages new password submission
    @PostMapping("/forgot-password/{id}")
    public ResponseEntity<Boolean> handleReset(
            @PathVariable String id,
            @RequestParam String password,
            @RequestParam String confirmPassword,
            Model model) {

        if (!password.equals(confirmPassword)) {
            model.addAttribute("error", "The passwords do not match.");
            model.addAttribute("userId", id);
            return ResponseEntity.ok(false);
        }

        String hashPassword = HashUtil.hashPassword(password);

        // Retrieve user or doctor
        boolean isDoctor = false;
        Optional<User> userOpt = userService.getUserById(id);
        User user = null;
        Doctor doctor = null;
        if (userOpt.isPresent()){
            user = userOpt.get();
        }
        else{
            Optional<Doctor> doctorOpt = doctorService.getDoctorById(id);
            if(doctorOpt.isPresent()){
                doctor = doctorOpt.get();
                isDoctor = true;
            }
        }

        if (user == null && doctor == null) {
            model.addAttribute("error", "User not found.");
            return ResponseEntity.ok(false);
        }

        // Save new password
        if (isDoctor) {
            doctor.setPassword(hashPassword);
            doctorService.updateDoctor(id, doctor);
        } else {
            user.setPassword(hashPassword);
            userService.updateUser(id, user);
        }

        model.addAttribute("message", "Password changed successfully. You can now log in.");
        return ResponseEntity.ok(true);
    }

    // Build Address object for the user
    private Address createAddress(HttpServletRequest request){
        // Extract address fields from request
        String street = request.getParameter("street");
        String city = request.getParameter("city");
        String province = request.getParameter("province");
        String CAP = request.getParameter("CAP");
        String country = request.getParameter("country");

        return new Address(street,city,province,CAP,country);
    }
}
