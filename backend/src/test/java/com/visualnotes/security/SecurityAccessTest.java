package com.visualnotes.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SecurityAccessTest {

    private static final String VALID_SECRET = "VisualNotesStudyPlannerProductionSecretKey2026Secure256Bit";
    private JwtUtil jwtUtil;

    @BeforeEach
    void setUp() {
        jwtUtil = new JwtUtil(VALID_SECRET, 3600000);
    }

    @Test
    void testTokenGenerationAndValidation() {
        String token = jwtUtil.generateToken("student@exam.edu", 42L);
        assertNotNull(token);
        assertTrue(jwtUtil.validateToken(token));
        assertEquals("student@exam.edu", jwtUtil.getEmailFromToken(token));
        assertEquals(42L, jwtUtil.getUserIdFromToken(token));
    }

    @Test
    void testInvalidTokenValidation() {
        assertFalse(jwtUtil.validateToken("invalid.token.structure"));
        assertFalse(jwtUtil.validateToken(""));
        assertFalse(jwtUtil.validateToken(null));
    }

    @Test
    void testShortKeyThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> {
            new JwtUtil("too-short-secret-key", 3600000);
        });
    }

    @Test
    void testTamperedTokenFailsValidation() {
        String token = jwtUtil.generateToken("student@exam.edu", 42L);
        String tamperedToken = token.substring(0, token.length() - 5) + "abcde";
        assertFalse(jwtUtil.validateToken(tamperedToken));
    }
}
