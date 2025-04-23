package it.unipi.healthhub.controller;

import it.unipi.healthhub.model.mongo.Doctor;
import it.unipi.healthhub.service.DoctorService;
import it.unipi.healthhub.util.ControllerUtil;

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

    // Handles requests to view a public profile of a doctor
    @GetMapping("/{id}")
    public String doctorPublicProfile(@PathVariable String id, Model model, HttpServletRequest request) {
        ControllerUtil.setSessionModel(model, request);
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
