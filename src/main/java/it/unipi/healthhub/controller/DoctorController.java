package it.unipi.healthhub.controller;

import it.unipi.healthhub.controller.api.DoctorAPI;
import it.unipi.healthhub.model.Doctor;
import it.unipi.healthhub.service.DoctorService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Optional;


@Controller
@RequestMapping("/doctors")
public class DoctorController {

    @Autowired
    private DoctorService doctorService;

    @GetMapping("/{id}")
    public String doctorPublicProfile(@PathVariable String id, Model model, HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            String role = (String) session.getAttribute("role");
            boolean isLoggedIn = (role != null);
            model.addAttribute("logged", isLoggedIn);
            model.addAttribute("role", isLoggedIn ? (role.equals("patient") ? "patient" : "doctor") : "");
        } else {
            model.addAttribute("logged", false);
            model.addAttribute("role", "");
        }
        // Get doctor by id
        Optional<Doctor> doctorOpt = doctorService.getDoctorById(id);
        if (doctorOpt.isPresent()) {
            Doctor doctor = doctorOpt.get();
            model.addAttribute("doctor", doctor);
            return "doctor-public";
        }
        return "doctor-public";
    }
}
