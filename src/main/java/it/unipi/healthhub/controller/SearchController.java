package it.unipi.healthhub.controller;

import it.unipi.healthhub.model.Doctor;
import it.unipi.healthhub.service.DoctorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import org.springframework.ui.Model;
import java.util.List;

@Controller
@RequestMapping("/search")
public class SearchController {
    @GetMapping
    public String search(Model model) {
        return "search";
    }
}
