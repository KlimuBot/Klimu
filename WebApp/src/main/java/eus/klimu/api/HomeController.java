package eus.klimu.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.ServletContextAware;

import javax.servlet.ServletContext;


@Controller
public class HomeController implements ServletContextAware {

    private ServletContext servletContext;

    @Override
    public void setServletContext(ServletContext servletContext) {
       this.servletContext = servletContext; 
    }

    @RequestMapping(path = {"/", "/index", "/home"})
    public String index(Model model) {
        model.addAttribute("title", "Bienvenido/a");
        model.addAttribute("message", "Pagina bienvenida!");
        return "index";
    }
}

