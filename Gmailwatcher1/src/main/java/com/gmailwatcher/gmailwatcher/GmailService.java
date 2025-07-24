package com.gmailwatcher.gmailwatcher;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import jakarta.mail.Address;
import jakarta.mail.BodyPart;
import jakarta.mail.Folder;
import jakarta.mail.Message;
import jakarta.mail.Session;
import jakarta.mail.Store;
import jakarta.mail.internet.MimeMultipart;

@Service
public class GmailService {

    private static final Logger log = LoggerFactory.getLogger(GmailService.class);

    @Value("${gmail.username}")
    private String email;

    @Value("${gmail.password}")
    private String password;

    private final WhatsAppService whatsAppService;
    private final StartupNotifier startupNotifier;

    public GmailService(WhatsAppService whatsAppService, StartupNotifier startupNotifier) {
        this.whatsAppService = whatsAppService;
        this.startupNotifier = startupNotifier;
    }

    @Scheduled(fixedRate = 300000) // Every 5 minutes
    public void checkGmailForAlerts() {
        try {
            log.info("🔍 Starting Gmail check for user: {}", email);
            List<String> keywords = Arrays.asList("urgent", "invoice", "alert", "payment");
            List<Message> matched = fetchRecentEmails(email, password, keywords, 50);

            if (!matched.isEmpty()) {
                // ✅ Send "Hello" alert only when keywords are found
                startupNotifier.sendInitialAlert();

                for (Message msg : matched) {
                    String subject = msg.getSubject();
                    Address[] fromAddrs = msg.getFrom();
                    String from = (fromAddrs != null && fromAddrs.length > 0) ? fromAddrs[0].toString() : "Unknown";

                    whatsAppService.sendWhatsAppAlert(
                        "+91number",
                        "📬 New Gmail Alert:\nFrom: " + from + "\nSubject: " + subject
                    );
                }

                log.info("✅ WhatsApp alerts sent for {} matching emails.", matched.size());
            } else {
                log.info("📭 No matching emails found.");
            }

        } catch (Exception e) {
            log.error("❌ Error while checking Gmail", e);
        }
    }

    public List<Message> fetchRecentEmails(String email, String password, List<String> keywords, int limit) throws Exception {
        log.info("📡 Connecting to Gmail via IMAP...");
        Properties props = new Properties();
        props.put("mail.store.protocol", "imaps");
        props.put("mail.debug", "false");

        Session session = Session.getInstance(props);
        List<Message> matchedMessages = new ArrayList<>();

        try (Store store = session.getStore("imaps")) {
            store.connect("imap.gmail.com", email, password);
            log.info("✅ Connected to Gmail successfully.");

            try (Folder inbox = store.getFolder("INBOX")) {
                inbox.open(Folder.READ_ONLY);
                log.info("📂 INBOX opened in READ_ONLY mode.");

                Message[] messages = inbox.getMessages();
                int total = messages.length;
                log.info("📨 Total emails in inbox: {}", total);

                int start = Math.max(1, total - limit + 1);
                Message[] recentMessages = inbox.getMessages(start, total);

                for (Message msg : recentMessages) {
                    if (msg == null) continue;

                    String subject = (msg.getSubject() != null) ? msg.getSubject().toLowerCase() : "";
                    String content = getTextFromMessage(msg).toLowerCase();

                    log.info("🧾 Checking email: {}", subject);

                    for (String keyword : keywords) {
                        if (subject.contains(keyword) || content.contains(keyword)) {
                            log.info("🔑 Keyword '{}' found in email: {}", keyword, subject);
                             startupNotifier.sendInitialAlert();
                            matchedMessages.add(msg);
                            break;
                        }
                    }
                }
            }
        } catch (Exception e) {
            log.error("❌ Exception during Gmail fetch", e);
            throw e;
        }

        return matchedMessages;
    }

    private String getTextFromMessage(Message message) throws Exception {
        if (message.isMimeType("text/plain")) {
            return message.getContent().toString();
        } else if (message.isMimeType("multipart/*")) {
            return getTextFromMimeMultipart((MimeMultipart) message.getContent());
        }
        return "";
    }

    private String getTextFromMimeMultipart(MimeMultipart mimeMultipart) throws Exception {
        StringBuilder result = new StringBuilder();
        int count = mimeMultipart.getCount();

        for (int i = 0; i < count; i++) {
            BodyPart part = mimeMultipart.getBodyPart(i);
            if (part.isMimeType("text/plain")) {
                result.append(part.getContent().toString());
            } else if (part.isMimeType("multipart/*")) {
                result.append(getTextFromMimeMultipart((MimeMultipart) part.getContent()));
            }
        }

        return result.toString();
    }
}
