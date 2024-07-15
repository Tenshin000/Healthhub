package it.unipi.healthhub.controller;

import it.unipi.healthhub.dao.mongo.Doctor;
import it.unipi.healthhub.dao.mongo.User;
import it.unipi.healthhub.service.DoctorService;
import it.unipi.healthhub.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class AuthController {

    @Autowired
    private DoctorService doctorService;

    @Autowired
    private UserService userService;

    @GetMapping("/login")
    public String loginForm() {
        return "login";
    }
    @PostMapping("/login")
    public String login(HttpServletRequest request, Model model) {
        String username = request.getParameter("username");
        String password = request.getParameter("password");

        HttpSession session = request.getSession();

        // Verifica se l'utente è un paziente
        User user = userService.loginUser(username, password);
        if (user != null) {
            // Imposta l'utente in sessione come paziente
            session.setAttribute("username", user.getUsername());
            session.setAttribute("patientId", user.getId());
            session.setAttribute("role", "patient");
            return "redirect:/index"; // Redirect dopo il login
        }

        // Verifica se l'utente è un medico
        Doctor doctor = doctorService.loginDoctor(username, password);
        if (doctor != null) {
            // Imposta l'utente in sessione come medico
            session.setAttribute("username", doctor.getUsername());
            session.setAttribute("doctorId", doctor.getId());
            session.setAttribute("role", "doctor");
            return "redirect:/doctors/dashboard"; // Redirect dopo il login
        }

        // Se nessun utente o medico trovato, mostra un messaggio di errore
        model.addAttribute("error", "Credenziali non valide");
        return "login";
    }

    @GetMapping("/logout")
    public String logout(HttpServletRequest request) {
        request.getSession().invalidate();
        return "redirect:/login";
    }

    @GetMapping("/register")
    public String registerPage(Model model) {
        model.addAttribute("user", new User());
        return "register";
    }

    @PostMapping("/register")
    public String registerUser(@ModelAttribute User user) {
        userService.createUser(user);
        return "redirect:/login";
    }

    @GetMapping("/register-doctor")
    public String registerPageDoctor(Model model) {
        model.addAttribute("doctor", new Doctor());
        return "register-doctor";
    }

    @PostMapping("/register-doctor")
    public String registerDoctor(@ModelAttribute Doctor doctor, Model model) {
        doctorService.createDoctor(doctor);
        return "redirect:/login";
    }
}
