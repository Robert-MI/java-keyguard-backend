package com.keyguard.backend;

import com.keyguard.backend.model.User;
import com.keyguard.backend.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootApplication
public class JavaKeyguardBackendApplication {

    @Bean
    CommandLineRunner initAdmin(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        return args -> {
            if (userRepository.findByUsername("admin").isEmpty()) {
                User admin = new User();
                admin.setUsername("admin");
                admin.setPassword(passwordEncoder.encode("supersecretpassword"));
                userRepository.save(admin);
                System.out.println("Admin user created with BCrypt hashing.");
            }
            if (userRepository.findByUsername("admin2").isEmpty()) {
                User admin = new User();
                admin.setUsername("admin2");
                admin.setPassword(passwordEncoder.encode("supersecretpassword2"));
                userRepository.save(admin);
                System.out.println("Admin user created with BCrypt hashing.");
            }
        };
    }

    public static void main(String[] args) {
        SpringApplication.run(JavaKeyguardBackendApplication.class, args);
    }

}
