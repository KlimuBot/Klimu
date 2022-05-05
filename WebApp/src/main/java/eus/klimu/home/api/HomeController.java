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

}

