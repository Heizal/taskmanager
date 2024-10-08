package com.example.taskmanager.controller;


import com.example.taskmanager.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestEmailController {
    @Autowired
    private EmailService emailService;

    @GetMapping("/test-email")
    public String sendTestEmail(@RequestParam String toEmail){
        emailService.sendEmail(toEmail, "Test Email", "This is a test email");
        return "Email sent successfully";
    }

}
