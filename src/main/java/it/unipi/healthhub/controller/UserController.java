package it.unipi.healthhub.controller;

import it.unipi.healthhub.model.mongo.Doctor;
import it.unipi.healthhub.service.UserService;
import it.unipi.healthhub.util.ControllerUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/user")
public class UserController {
    @Autowired
    private UserService userService;

    // Display the profile of the user
    @GetMapping("/profile")
    public String profile(Model model, HttpServletRequest request) {
        ControllerUtil.setSessionModel(model, request);
        return "user-profile";
    }

    // Display the appointments of the user
    @GetMapping("/appointments")
    public String appointments(Model model, HttpServletRequest request) {
        ControllerUtil.setSessionModel(model, request);
        return "user-appointments";
    }

    @GetMapping("/favorites")
    public String favorites(Model model, HttpServletRequest request) {
        ControllerUtil.setSessionModel(model, request);
        String userId = (String) request.getSession().getAttribute("patientId");
        List<Doctor> endorsedDoctors = userService.getEndorsedDoctors(userId);
        model.addAttribute("endorsedDoctors", endorsedDoctors);
        List<Doctor> reviewedDoctors = userService.getReviewedDoctors(userId);
        model.addAttribute("reviewedDoctors", reviewedDoctors);
        return "user-favorites";
    }
}
