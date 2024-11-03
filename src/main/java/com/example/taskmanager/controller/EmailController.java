package com.example.taskmanager.controller;
import com.example.taskmanager.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/email")
public class EmailController {
    @Autowired
    private EmailService emailService;

    @PostMapping("/send")
    public String sendEmail(@AuthenticationPrincipal OidcUser principal, @RequestBody EmailRequest emailRequest) throws Exception {
        if (principal == null) {
            throw new RuntimeException("User is not authenticated, principal is null.");
        }
        //Get the logged-in users email and access token
        String userEmail = principal.getEmail();
        String accessToken = principal.getIdToken().getTokenValue();

        //Call emailService to send email on behalf of user
        emailService.sendEmailWithGmailApi(userEmail, emailRequest.getTo(), emailRequest.getSubject(), emailRequest.getBody(), accessToken);

        return "Email sent successfully!";
    }


    // DTO to map email request payload
    public static class EmailRequest {
        private String to;
        private String subject;
        private String body;

        // Getters and Setters
        public String getTo() {
            return to;
        }

        public void setTo(String to) {
            this.to = to;
        }

        public String getSubject() {
            return subject;
        }

        public void setSubject(String subject) {
            this.subject = subject;
        }

        public String getBody() {
            return body;
        }

        public void setBody(String body) {
            this.body = body;
        }
    }
}
