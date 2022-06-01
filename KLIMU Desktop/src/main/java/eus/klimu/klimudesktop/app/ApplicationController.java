package eus.klimu.klimudesktop.app;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.ArrayList;

@Slf4j
@Controller
@RequestMapping("/klimu")
public class ApplicationController {

    @GetMapping("/app")
    public String getMainPage(Model model) {
        model.addAttribute("notifications", new ArrayList<>());

        return "index";
    }

    @GetMapping(value = "/login")
    public String getLoginPage() {
        return "login";
    }

}
