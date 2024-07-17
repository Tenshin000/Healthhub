package it.unipi.healthhub.controller;

import it.unipi.healthhub.util.ControllerUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/user")
public class UserController {

    @GetMapping("/profile")
    public String profile(Model model, HttpServletRequest request) {
        ControllerUtil.setSessionModel(model, request);
        return "user-profile";
    }



    @GetMapping("/appointments")
    public String appointments(Model model, HttpServletRequest request) {
        ControllerUtil.setSessionModel(model, request);
        return "user-appointments";
    }

    @GetMapping("/favorites")
    public String favorites(Model model, HttpServletRequest request) {
        ControllerUtil.setSessionModel(model, request);
        return "user-favorites";
    }
}
