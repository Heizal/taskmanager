package com.example.taskmanager.service;

import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.model.Message;
import jakarta.mail.Session;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizedClientRepository;
import org.springframework.stereotype.Service;

import java.util.Base64;
import java.util.Properties;

@Service
public class EmailService {
    private final Gmail gmailService;
    private final OAuth2AuthorizedClientRepository authorizedClientRepository;
    private final ClientRegistrationRepository clientRegistrationRepository;

    @Autowired
    public EmailService(Gmail gmailService,
                        OAuth2AuthorizedClientRepository authorizedClientRepository,
                        ClientRegistrationRepository clientRegistrationRepository) {
        this.gmailService = gmailService;
        this.authorizedClientRepository = authorizedClientRepository;
        this.clientRegistrationRepository = clientRegistrationRepository;
    }

    // Send email on behalf of the user using Gmail API and access token
    public void sendEmailWithGmailApi(String userEmail, String to, String subject, String body, String accessToken) throws Exception {
        // Create the MimeMessage
        MimeMessage mimeMessage = createEmail(to, userEmail, subject, body);

        // Send the email using the Gmail API
        sendMessage(gmailService, userEmail, mimeMessage, accessToken);
    }

    // Create an email using javax.mail
    public MimeMessage createEmail(String to, String from, String subject, String bodyText) throws Exception {
        Properties props = new Properties();
        Session session = Session.getDefaultInstance(props, null);

        MimeMessage email = new MimeMessage(session);
        email.addRecipient(jakarta.mail.Message.RecipientType.TO, new jakarta.mail.internet.InternetAddress(to));
        email.setFrom(new jakarta.mail.internet.InternetAddress(from));
        email.setSubject(subject);
        email.setText(bodyText);
        return email;
    }

    // Send the email using Gmail API
    public void sendMessage(Gmail service, String userId, MimeMessage email, String accessToken) throws Exception {
        Message message = createMessageWithEmail(email);

        // Set the Authorization header with the access token
        service.users().messages().send(userId, message).setOauthToken(accessToken).execute();
    }

    // Helper method to encode the email content into Gmail API Message format
    public Message createMessageWithEmail(MimeMessage email) throws Exception {
        java.io.ByteArrayOutputStream buffer = new java.io.ByteArrayOutputStream();
        email.writeTo(buffer);
        byte[] bytes = buffer.toByteArray();
        String encodedEmail = Base64.getUrlEncoder().encodeToString(bytes);
        Message message = new Message();
        message.setRaw(encodedEmail);
        return message;
    }
}
