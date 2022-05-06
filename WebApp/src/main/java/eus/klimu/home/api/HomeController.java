package eus.klimu.home.api;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class HomeController {

    @RequestMapping(path = {"/", "/index", "/home"})
    public String index(Model model) {
        model.addAttribute("title", "Bienvenido/a");
        model.addAttribute("message", "Pagina bienvenida!");
        return "index";
    }

    @RequestMapping(path = {"/email"})
    public String email(Model model) {
        return "services/email";
    }
    
    @GetMapping("/login/sign-in")
    public String getLoginPage() {
        return "users/login";
    }

    @RequestMapping(path = {"/alerts"})
    public String alerts(Model model){return "services/listaAlertas";}

    @RequestMapping(path = {"/subscription"})
    public String subscription(Model model){return "services/suscripciones";}
    
}

