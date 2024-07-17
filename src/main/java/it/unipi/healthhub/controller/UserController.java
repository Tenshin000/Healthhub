package it.unipi.healthhub.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/user")
public class UserController {
    @GetMapping("/profile")
    public String profile() {
        return "user-profile";
    }

    @GetMapping("/appointments")
    public String appointments() {
        return "user-appointments";
    }

    @GetMapping("/favorites")
    public String favorites() {
        return "user-favorites";
    }
}
