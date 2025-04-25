package it.unipi.healthhub.controller;

import it.unipi.healthhub.model.mongo.Address;
import it.unipi.healthhub.model.mongo.Doctor;
import it.unipi.healthhub.model.mongo.User;
import it.unipi.healthhub.service.DoctorService;
import it.unipi.healthhub.service.UserService;
import it.unipi.healthhub.util.ControllerUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.ArrayList;
import java.util.List;

@Controller
public class AuthController {

    @Autowired
    private DoctorService doctorService;

    @Autowired
    private UserService userService;

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

        HttpSession session = request.getSession();

        // Check if the user is a patient
        User user = userService.loginUser(username, password);
        if (user != null) {
            // Set session attributes for the patient
            session.setAttribute("username", user.getUsername());
            session.setAttribute("patientId", user.getId());
            session.setAttribute("role", "patient");
            return "redirect:/index"; // Redirect to home page after login
        }

        // Check if the user is a doctor
        Doctor doctor = doctorService.loginDoctor(username, password);
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
    public String registerUser(HttpServletRequest request, @ModelAttribute User user) {
        user.setAddress(createAddress(request));
        String phone = request.getParameter("phone");
        user.setPersonalNumber(phone);
        userService.createUser(user);
        return "redirect:/login";
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
    public String registerDoctor(HttpServletRequest request, @ModelAttribute Doctor doctor, Model model){
        // Get the specialization string from the form (e.g. "Cardiology, Pediatrics, Neurology")
        String specializationString = request.getParameter("specialization");

        if(specializationString != null && !specializationString.isBlank()){
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

        if(phoneNumber != null && !phoneNumber.isBlank()){
            // Add the single phone number as a one-element list
            List<String> phoneList = new ArrayList<>();
            phoneList.add(phoneNumber.trim());

            // Set the phone number list to the doctor object
            doctor.setPhoneNumbers(phoneList);
        }

        doctor.setAddress(createAddress(request));

        doctorService.createDoctor(doctor);
        return "redirect:/login";
    }
}
