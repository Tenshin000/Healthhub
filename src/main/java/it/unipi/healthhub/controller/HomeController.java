package it.unipi.healthhub.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import jakarta.servlet.http.HttpSession;

@Controller
public class HomeController {
    @GetMapping({"/", "/index"})
    public String index(Model model, HttpServletRequest request) {
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
        return "index";
    }
    @GetMapping("/search")
    public String search(Model model, HttpServletRequest request) {
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
        return "search";
    }

}
