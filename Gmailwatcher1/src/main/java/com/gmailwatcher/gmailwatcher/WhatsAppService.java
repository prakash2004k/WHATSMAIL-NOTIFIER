package com.gmailwatcher.gmailwatcher;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;

import jakarta.annotation.PostConstruct;

@Service
public class WhatsAppService {

    private static final Logger logger = LoggerFactory.getLogger(WhatsAppService.class);

    @Value("${twilio.account.sid}")
    private String accountSid;

    @Value("${twilio.auth.token}")
    private String authToken;

    @Value("${twilio.whatsapp.from}")
    private String fromNumber; // e.g. whatsapp:+1307....

    @PostConstruct
    public void initTwilio() {
        Twilio.init(accountSid, authToken);
        logger.info("✅ Twilio initialized");
    }

    public void sendWhatsAppAlert(String toNumber, String messageText) {
        try {
            logger.info("📤 Sending WhatsApp message to: {}", toNumber);
            Message message = Message.creator(
                    new PhoneNumber("whatsapp:" + toNumber), // use dynamic recipient
                    new PhoneNumber(fromNumber),              // sender number from config
                    messageText
            ).create();
            logger.info("✅ WhatsApp message sent. SID: {}", message.getSid());
        } catch (Exception e) {
           logger.error("❌ Error sending WhatsApp message: {}", e.getMessage(), e);
        }
    }
}
