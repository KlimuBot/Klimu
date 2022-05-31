package eus.klimu.klimudesktop;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Bean;
import org.springframework.context.event.EventListener;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

@Slf4j
@SpringBootApplication
public class KlimuDesktopApplication {

    private static final String URL = "http://localhost:8080/login";

    public static void main(String[] args) {
        SpringApplication.run(KlimuDesktopApplication.class, args);
    }

    @EventListener({ApplicationReadyEvent.class})
    public void applicationReadyEvent() {
        log.info("Application started, launching browser.");
        openBrowser();
    }

    private void openBrowser() {
        if(Desktop.isDesktopSupported()){
            Desktop desktop = Desktop.getDesktop();
            try {
                desktop.browse(new URI(URL));
            } catch (URISyntaxException | IOException e) {
                e.printStackTrace();
            }
        }else{
            Runtime runtime = Runtime.getRuntime();
            try {
                runtime.exec("rundll32 url.dll,FileProtocolHandler " + URL);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
