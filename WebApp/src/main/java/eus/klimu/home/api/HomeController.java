package eus.klimu.home.api;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@Slf4j
@Controller
public class HomeController {

    @GetMapping(path = {"/", "/index", "/home"})
    public String index() {
        return "index";
    }
    
    @GetMapping("/login/sign-in")
    public String getLoginPage(
            Model model,
            @RequestParam(value = "error", required = false) String error
    ) {
        if (error != null && error.length() > 1) {
            model.addAttribute("errorMsg", error);
        }
        else {
            model.addAttribute("errorMsg", null);
        }
        return "users/login";
    }

    @GetMapping("/logout")
    public void logout(HttpServletRequest request, HttpServletResponse response) throws IOException {
        HttpSession session = request.getSession();

        session.removeAttribute("accessToken");
        session.removeAttribute("refreshToken");

        // Send response
        response.setStatus(HttpServletResponse.SC_OK);
        response.sendRedirect("/");
    }

    /*
    * De aqui para abajo es temporal, habra que mover cada cosa a su controlador.
    * De momento se queda aqui hasta que este listo el modelo entidad relacion.
    */
    @GetMapping("/email")
    public String email() {
        return "services/email";
    }

    @GetMapping("/subscription/alerts/{type}")
    public String alerts(@PathVariable String type) {
        switch (type) {
            case "telegram": break;
            default: break;
        }
        return "services/listaAlertas";
    }

    @GetMapping("/subscription")
    public String subscription() {
        return "services/suscripciones";
    }
}

