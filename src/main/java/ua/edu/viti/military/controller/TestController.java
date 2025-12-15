package ua.edu.viti.military.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import ua.edu.viti.military.entity.User;
import ua.edu.viti.military.repository.UserRepository;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/test")
@RequiredArgsConstructor
public class TestController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @GetMapping("/check-admin")
    public Map<String, Object> checkAdmin() {
        Map<String, Object> result = new HashMap<>();
        
        User admin = userRepository.findByUsername("admin").orElse(null);
        
        if (admin == null) {
            result.put("found", false);
            result.put("message", "Admin user not found");
        } else {
            result.put("found", true);
            result.put("username", admin.getUsername());
            result.put("email", admin.getEmail());
            result.put("enabled", admin.getEnabled());
            result.put("roles", admin.getRoles().size());
            result.put("passwordHash", admin.getPassword());
            
            // Test password
            boolean matches = passwordEncoder.matches("admin123", admin.getPassword());
            result.put("passwordMatches", matches);
        }
        
        return result;
    }

    @GetMapping("/generate-hash")
    public Map<String, String> generateHash(@RequestParam String password) {
        String hash = passwordEncoder.encode(password);
        return Map.of("password", password, "hash", hash);
    }
}
