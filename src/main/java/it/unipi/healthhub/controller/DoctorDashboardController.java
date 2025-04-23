package it.unipi.healthhub.controller;

import it.unipi.healthhub.service.DoctorService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/doctor/dashboard")
public class DoctorDashboardController {

    @Autowired
    private DoctorService doctorService;

    // Main dashboard page for the doctor
    @GetMapping
    public String dashboard(Model model, HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        // Retrieve doctor object using the ID stored in session
        model.addAttribute(
                "doctor",
                doctorService.getDoctorById(
                        session.getAttribute("doctorId").toString()
                ).get()
        );
        return "doctor-dashboard";
    }

    // Page for viewing the doctor's appointments
    @GetMapping("/appointments")
    public String appointments(Model model, HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        // Fetch doctor details from session and pass to the model
        model.addAttribute(
                "doctor",
                doctorService.getDoctorById(
                        session.getAttribute("doctorId").toString()
                ).get()
        );
        return "doctor-appointments";
    }

    // Page for viewing the doctor's profile
    @GetMapping("/profile")
    public String profile(Model model, HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        // Add doctor object to the model for the profile view
        model.addAttribute(
                "doctor",
                doctorService.getDoctorById(
                        session.getAttribute("doctorId").toString()
                ).get()
        );
        return "doctor-profile";
    }

    // Page for viewing reviews received by the doctor
    @GetMapping("/reviews")
    public String reviews(Model model, HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        // Load doctor data and attach it to the model
        model.addAttribute(
                "doctor",
                doctorService.getDoctorById(
                        session.getAttribute("doctorId").toString()
                ).get()
        );
        return "doctor-reviews";
    }

    // Page for viewing and managing templates
    @GetMapping("/templates")
    public String templates(Model model, HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        // Retrieve and pass the doctor information to the view
        model.addAttribute(
                "doctor",
                doctorService.getDoctorById(
                        session.getAttribute("doctorId").toString()
                ).get()
        );
        return "doctor-templates";
    }

    // Page showing the doctor's weekly schedule
    @GetMapping("/week")
    public String week(Model model, HttpServletRequest request) {
        HttpSession session = request.getSession(false);

        // Load doctor from the session and attach to the model
        model.addAttribute(
                "doctor",
                doctorService.getDoctorById(
                        session.getAttribute("doctorId").toString()
                ).get()
        );
        return "doctor-week";
    }
}
