package com.gmailwatcher.gmailwatcher;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.mail.Flags;
import jakarta.mail.Message;

@RestController
@RequestMapping("/api")
public class WatcherController {

    private static final Logger log = LoggerFactory.getLogger(WatcherController.class);

    @Autowired
    private GmailService gmailService;

    @Autowired
    private WhatsAppService whatsAppService;

    @PostMapping("/watch")
    public String watchEmails(@RequestBody WatchRequest request) throws Exception {
        System.out.println(">>> POST /api/watch called");
        System.out.println(">>> Request received:");
        System.out.println(">>> watchEmails() endpoint hit");

        System.out.println("Email: " + request.getEmail());
        System.out.println("Keywords: " + request.getKeywords());
        System.out.println("Delay: " + request.getDelay());
        System.out.println("Phone: " + request.getPhone());

        // Fetch the latest 50 emails and match for keywords
        List<Message> messages = gmailService.fetchRecentEmails(
            request.getEmail(),
            request.getPassword(),
            request.getKeywords(),
            50 // limit to check last 50 emails only
        );

        System.out.println(">>> Total matched recent emails: " + messages.size());

        for (Message msg : messages) {
            String subject = msg.getSubject(); // cache subject
            boolean isUnread = !msg.isSet(Flags.Flag.SEEN); // cache read status

            System.out.println(">>> Scheduling alert for subject: " + subject);

            new Timer().schedule(new TimerTask() {
                @Override
                public void run() {
                    try {
                        if (isUnread) {
                            System.out.println(">>> Message still unread: " + subject);
                            System.out.println(">>> Sending WhatsApp alert to: " + request.getPhone());

                            whatsAppService.sendWhatsAppAlert(
                                request.getPhone(),
                                "📨 Unopened Mail:\nSubject: " + subject
                            );
                        } else {
                            System.out.println(">>> Message already read: " + subject);
                        }
                    } catch (Exception e) {
                        log.error("❌ Error while sending WhatsApp alert", e);
                    }
                }
            }, request.getDelay() * 60 * 1000L); // Delay in milliseconds
        }

        return messages.size() + " matching recent mails found. Watching for status...";
    }
}
