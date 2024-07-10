package it.unipi.healthhub.controller;

import it.unipi.healthhub.model.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class HomeController {
    @GetMapping({"/", "/index"})
    public String index(Model model) {
        return "index";
    }
    @GetMapping("/search")
    public String search(Model model) {
        return "search";
    }

}
