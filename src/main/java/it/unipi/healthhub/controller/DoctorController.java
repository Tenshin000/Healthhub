package it.unipi.healthhub.controller;

import it.unipi.healthhub.model.Doctor;
import it.unipi.healthhub.service.DoctorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class DoctorController {

    @Autowired
    private DoctorService doctorService;

    @GetMapping("/doctor/signup")
    public String showSignupForm(Model model) {
        model.addAttribute("doctor", new Doctor());
        return "doctor-signup";
    }

    @PostMapping("/doctor/signup")
    public String signupDoctor(@ModelAttribute Doctor doctor, Model model) {
        doctorService.createDoctor(doctor);
        model.addAttribute("message", "Registrazione avvenuta con successo");
        return "redirect:/doctor/signup-success";
    }

    @GetMapping("/doctor/signup-success")
    public String signupSuccess(Model model) {
        model.addAttribute("message", "Registrazione avvenuta con successo");
        return "doctor-signup-success";
    }
}
