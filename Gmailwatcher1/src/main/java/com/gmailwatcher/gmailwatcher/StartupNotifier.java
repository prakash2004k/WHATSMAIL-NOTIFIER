package com.gmailwatcher.gmailwatcher;

import org.springframework.stereotype.Component;

@Component
public class StartupNotifier {

    private final WhatsAppService whatsAppService;

    public StartupNotifier(WhatsAppService whatsAppService) {
        this.whatsAppService = whatsAppService;
    }

    public void sendInitialAlert() {
        whatsAppService.initTwilio();
        whatsAppService.sendWhatsAppAlert("number", "check your email u have an important message...");
    }
}
