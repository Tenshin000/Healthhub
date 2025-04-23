package it.unipi.healthhub.controller;

import it.unipi.healthhub.util.ControllerUtil;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import jakarta.servlet.http.HttpSession;


@Controller
public class HomeController {
    // Display index page
    @GetMapping({"/", "/index"})
    public String index(Model model, HttpServletRequest request) {
        ControllerUtil.setSessionModel(model, request);
        return "index";
    }

    // Display search page
    @GetMapping("/search")
    public String search(Model model, HttpServletRequest request) {
        ControllerUtil.setSessionModel(model, request);
        return "search";
    }

}
