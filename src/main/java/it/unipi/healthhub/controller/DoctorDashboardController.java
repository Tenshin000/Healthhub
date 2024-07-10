package it.unipi.healthhub.controller;

import it.unipi.healthhub.service.DoctorService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/doctors/dashboard")
public class DoctorDashboardController {

    @Autowired
    private DoctorService doctorService;
    @GetMapping
    public String dashboard(Model model, HttpSession session) {
        model.addAttribute(
                "doctor",
                doctorService.getDoctorById(
                        session.getAttribute("doctorId").toString()
                ).get()
        );
        return "doctor-dashboard";
    }
    @GetMapping("/appointments")
    public String appointments(Model model, HttpSession session) {
        model.addAttribute(
                "doctor",
                doctorService.getDoctorById(
                        session.getAttribute("doctorId").toString()
                ).get()
        );
        return "doctor-appointments";
    }

    @GetMapping("/profile")
    public String profile(Model model, HttpSession session) {
        model.addAttribute(
                "doctor",
                doctorService.getDoctorById(
                        session.getAttribute("doctorId").toString()
                ).get()
        );
        return "doctor-profile";
    }
    @GetMapping("/reviews")
    public String reviews(Model model, HttpSession session) {
        model.addAttribute(
                "doctor",
                doctorService.getDoctorById(
                        session.getAttribute("doctorId").toString()
                ).get()
        );
        return "doctor-reviews";
    }

    @GetMapping("/templates")
    public String templates(Model model, HttpSession session) {
        model.addAttribute(
                "doctor",
                doctorService.getDoctorById(
                        session.getAttribute("doctorId").toString()
                ).get()
        );
        return "doctor-templates";
    }

    @GetMapping("/week")
    public String week(Model model, HttpSession session) {
        model.addAttribute(
                "doctor",
                doctorService.getDoctorById(
                        session.getAttribute("doctorId").toString()
                ).get()
        );
        return "doctor-week";
    }

}
