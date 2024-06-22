package it.unipi.healthhub.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;


@Controller
public class DashboardController {

    @GetMapping("/dashboard")
    public String dashboard(HttpServletRequest request, Model model) {
        // Verifica se l'utente ha una sessione attiva
        String username = (String) request.getSession().getAttribute("username");

        if (username != null) {
            model.addAttribute("username", username);
            return "dashboard"; // Nome della pagina HTML per il dashboard
        } else {
            return "redirect:/login"; // Reindirizza alla pagina di login se l'utente non è autenticato
        }
    }
}
