package io.github.danarrigo.if20502026k01g1doneate;

import io.github.danarrigo.if20502026k01g1doneate.boundaries.LoginUI;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class If2050Application {

    public static void main(String[] args) {
        System.out.println("[INFO] Starting DONE-ATE Application...");
        
        // Start Spring Boot backend in a separate thread
        Thread springThread = new Thread(() -> {
            try {
                SpringApplication.run(If2050Application.class, args);
            } catch (Exception e) {
                System.err.println("[ERROR] Spring Boot failed to start: " + e.getMessage());
                e.printStackTrace();
            }
        });
        springThread.setDaemon(false);
        springThread.start();
        
        System.out.println("[INFO] Launching Login Interface...");
        // Start JavaFX UI
        new LoginUI().showUI();
    }

}
