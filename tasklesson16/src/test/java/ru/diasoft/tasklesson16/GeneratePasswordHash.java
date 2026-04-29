package ru.diasoft.tasklesson16;

import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class GeneratePasswordHash {
    
    @Test
    public void generateHashes() {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        
        String userPassword = encoder.encode("user");
        String adminPassword = encoder.encode("admin");
        
        System.out.println("=".repeat(80));
        System.out.println("BCrypt hashes for passwords:");
        System.out.println("=".repeat(80));
        System.out.println("user password hash:  " + userPassword);
        System.out.println("admin password hash: " + adminPassword);
        System.out.println("=".repeat(80));
        
        // Verify they work
        System.out.println("\nVerification:");
        System.out.println("user matches: " + encoder.matches("user", userPassword));
        System.out.println("admin matches: " + encoder.matches("admin", adminPassword));
    }
}
