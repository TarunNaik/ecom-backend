package com.example.ecommerce.service;

import com.example.ecommerce.model.PasswordResetToken;
import com.example.ecommerce.model.User;
import com.example.ecommerce.repository.PasswordResetTokenRepository;
import com.example.ecommerce.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class PasswordResetService {

    @Autowired
    private PasswordResetTokenRepository passwordResetTokenRepository;

    @Autowired
    private EmailService emailService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    public void processForgotPassword(String email) {
        String token = UUID.randomUUID().toString(); // e.g., "a3f5b7c9-..."
        LocalDateTime expiry = LocalDateTime.now().plusHours(1); // Valid for 1 hour

        PasswordResetToken resetToken = PasswordResetToken.builder()
                .email(email)
                .token(token)
                .expiryDate(expiry)
                .build();
        passwordResetTokenRepository.save(resetToken);
        String resetLink = "http://localhost:3000/reset-password?token=" + token;
        emailService.sendResetEmail(email, resetLink);
    }

    public ResponseEntity<String> processResetPassword(String token, String newPassword) {
        PasswordResetToken resetToken = passwordResetTokenRepository.findByToken(token);
        if (resetToken == null || resetToken.isExpired()) {
            return ResponseEntity.badRequest().body("Invalid or expired token");
        }

        // Update user password
        User user = userRepository.findByEmail(resetToken.getEmail());
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        // Delete used token
        passwordResetTokenRepository.delete(resetToken);
        return ResponseEntity.ok("Password reset successful");
    }
}
