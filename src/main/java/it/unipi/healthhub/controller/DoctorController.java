package it.unipi.healthhub.controller;

import it.unipi.healthhub.model.Doctor;
import it.unipi.healthhub.service.DoctorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/doctor")
public class DoctorController {

    @Autowired
    private DoctorService doctorService;
    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        return "doctor-dashboard";
    }
    @GetMapping("/appointments")
    public String appointments(Model model) {
        return "doctor-appointments";
    }

    @GetMapping("/profile")
    public String profile(Model model) {
        return "doctor-profile";
    }
    @GetMapping("/reviews")
    public String reviews(Model model) {
        return "doctor-reviews";
    }

    @GetMapping("/templates")
    public String templates(Model model) {
        return "doctor-templates";
    }

}
