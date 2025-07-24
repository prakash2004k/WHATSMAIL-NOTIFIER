package com.gmailwatcher.gmailwatcher.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "twilio")
public class TwilioConfig {

    private String accountSid;
    private String authToken;
    private String whatsappFrom;

    // Getters and setters
    public String getAccountSid() {
        return accountSid;
    }

    public void setAccountSid(String accountSid) {
        this.accountSid = accountSid;
    }

    public String getAuthToken() {
        return authToken;
    }

    public void setAuthToken(String authToken) {
        this.authToken = authToken;
    }

    public String getWhatsappFrom() {
        return whatsappFrom;
    }

    public void setWhatsappFrom(String whatsappFrom) {
        this.whatsappFrom = whatsappFrom;
    }
}
