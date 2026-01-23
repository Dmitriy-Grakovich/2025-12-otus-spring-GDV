package ru.diasoft.tasklesson16;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
public class DebugPasswordTest {
    
    @Autowired
    private JdbcTemplate jdbcTemplate;
    
    @Test
    public void checkPasswordInDatabase() {
        String password = jdbcTemplate.queryForObject(
            "SELECT password FROM consumer WHERE username = 'user'", 
            String.class
        );
        
        System.out.println("=".repeat(80));
        System.out.println("Password in database for user 'user': [" + password + "]");
        System.out.println("Password length: " + password.length());
        System.out.println("First char: " + (int)password.charAt(0));
        System.out.println("=".repeat(80));
    }
}
