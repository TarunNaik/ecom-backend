package com.example.ecommerce.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {
    private static final Logger logger = LoggerFactory.getLogger(EmailService.class);
//    @Autowired
//    private JavaMailSender mailSender;

    public void sendResetEmail(String toEmail, String resetLink) {
        // Implementation for sending email
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom("your-email@gmail.com");
            message.setTo(toEmail);
            message.setSubject("Password Reset Request - Faguni Fashion Mall");
            message.setText("Hello,\n\n" +
                    "You requested to reset your password.\n\n" +
                    "Click the link below to reset your password:\n" +
                    resetLink + "\n\n" +
                    "This link will expire in 1 hour.\n\n" +
                    "If you didn't request this, please ignore this email.\n\n" +
                    "Best regards,\n" +
                    "Faguni Fashion Mall Team");

//            mailSender.send(message);
            logger.info("Reset email sent to: {}", toEmail);

        } catch (Exception e) {
            logger.error("Failed to send email: {}", e.getMessage());
            throw new RuntimeException("Failed to send reset email", e);
        }
    }


}
