package com.example.ecommerce.service;

import com.example.ecommerce.model.User;
import com.example.ecommerce.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
public class AuthService {

    private static final String IMAGE_DIR = "src/main/resources/images/";

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    public ResponseEntity<String> updateUserProfile(MultipartFile file, String name, String email) throws SecurityException, IOException {

        User user = userRepository.findByEmail(email);
        if (user == null) {
            return new ResponseEntity<>("User not found", HttpStatus.NOT_FOUND);
        }
        if(name != null && !name.isEmpty()) {
            user.setName(name);
        }

        if (file != null && !file.isEmpty()) {
            String fileName = "user_" + user.getId() + "_profile_img." + getFileExtension(file.getOriginalFilename());
            Path imagePath = Paths.get(IMAGE_DIR + fileName);

            // Save image to local resources/images
            Files.createDirectories(imagePath.getParent());
            Files.write(imagePath, file.getBytes());
            user.setImageUrl(fileName);

        }
        userRepository.save(user);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    private String getFileExtension(String originalFilename) {
        if (originalFilename != null && originalFilename.contains(".")) {
            return originalFilename.substring(originalFilename.lastIndexOf('.') + 1);
        }
        return "";
    }
}
