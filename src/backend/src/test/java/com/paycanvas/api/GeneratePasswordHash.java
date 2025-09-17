package com.paycanvas.api;

import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class GeneratePasswordHash {
    @Test
    public void generateHash() {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String correctHash = encoder.encode("password");
        System.out.println("UPDATE m_users SET password_hash = '" + correctHash + "';");
    }
}