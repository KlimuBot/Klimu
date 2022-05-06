package eus.klimu.security.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
@Controller
public class ApiErrorHandler implements ErrorController {

    @RequestMapping("/error/{errorCode}")
    public void redirectErrorPage(
            HttpServletResponse response,
            @PathVariable int errorCode
    ) throws IOException {
        if (errorCode == HttpStatus.FORBIDDEN.value()) {
            response.sendRedirect("/login/sign-in/You-must-be-logged-to-access-this-page");
        }
    }

}
