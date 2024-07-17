package it.unipi.healthhub.util;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.ui.Model;

public class ControllerUtil {
    public static void setSessionModel(Model model, HttpServletRequest request) {
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
    }
}
