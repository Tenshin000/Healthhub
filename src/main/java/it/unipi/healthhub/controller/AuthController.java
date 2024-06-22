package it.unipi.healthhub.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class AuthController {

    @GetMapping("/login")
    public String loginForm() {
        return "login"; // Nome della pagina HTML per il form di login
    }

    @PostMapping("/login")
    public String login(HttpServletRequest request, Model model) {
        String username = request.getParameter("username");
        String password = request.getParameter("password");

        // Esempio di logica per verificare l'utente (sostituire con la tua logica)
        if ("admin".equals(username) && "password".equals(password)) {
            // Imposta l'utente in sessione (esempio semplice)
            request.getSession().setAttribute("username", username);
            return "redirect:/dashboard"; // Redirect dopo il login
        } else {
            model.addAttribute("error", "Credenziali non valide");
            return "login";
        }
    }

    @GetMapping("/logout")
    public String logout(HttpServletRequest request) {
        // Invalida la sessione e reindirizza alla pagina di login
        request.getSession().invalidate();
        return "redirect:/login";
    }
}
