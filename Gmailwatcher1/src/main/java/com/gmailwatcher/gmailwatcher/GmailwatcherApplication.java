package com.gmailwatcher.gmailwatcher;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import jakarta.annotation.PostConstruct;

@SpringBootApplication
public class GmailwatcherApplication {

    private final WhatsAppService whatsAppService;

    // ✅ Constructor injection
    public GmailwatcherApplication(WhatsAppService whatsAppService) {
        this.whatsAppService = whatsAppService;
    }

    public static void main(String[] args) {
        SpringApplication.run(GmailwatcherApplication.class, args);
    }

    @PostConstruct
    public void startApp() {
        whatsAppService.initTwilio();
        whatsAppService.sendWhatsAppAlert("number", " Checking the Mailss....");
    }
}
