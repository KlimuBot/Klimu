package eus.klimu.klimudesktop.app;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Slf4j
@Controller
public class ApplicationController {

    @GetMapping(value = "/")
    public String getLoginPage() {
        return "login";
    }

}
