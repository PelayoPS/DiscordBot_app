package com.discordbot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import javax.swing.SwingUtilities;

@SpringBootApplication
public class DiscordBotApplication {
    public static void main(String[] args) {
        // Iniciar la GUI
        SwingUtilities.invokeLater(() -> {
            BackendGUI gui = new BackendGUI();
            gui.setVisible(true);
        });

        // Iniciar Spring Boot
        SpringApplication.run(DiscordBotApplication.class, args);
    }
}