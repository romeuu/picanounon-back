package com.picanounon.back;

import com.picanounon.back.model.AuthProvider;
import com.picanounon.back.model.Role;
import com.picanounon.back.model.User;
import com.picanounon.back.security.jwt.JwtTokenProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;

class JwtTokenProviderTest {

    private JwtTokenProvider jwtTokenProvider;

    @BeforeEach
    void setUp() {
        jwtTokenProvider = new JwtTokenProvider();
        ReflectionTestUtils.setField(jwtTokenProvider, "jwtSecret", "404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970");
        ReflectionTestUtils.setField(jwtTokenProvider, "jwtExpirationMs", 3600000L); // 1 hour
        jwtTokenProvider.init();
    }

    @Test
    void testCreateAndValidateToken() {
        User user = User.builder()
                .id(1L)
                .email("test@picanounon.com")
                .name("Test User")
                .role(Role.ROLE_USER)
                .provider(AuthProvider.GOOGLE)
                .build();

        String token = jwtTokenProvider.createToken(user);
        assertNotNull(token);
        assertTrue(jwtTokenProvider.validateToken(token));

        String email = jwtTokenProvider.getEmailFromToken(token);
        assertEquals("test@picanounon.com", email);
    }

    @Test
    void testInvalidToken() {
        assertFalse(jwtTokenProvider.validateToken("invalid.token.string"));
    }

    @Test
    void testExpiredToken() {
        JwtTokenProvider expiredProvider = new JwtTokenProvider();
        ReflectionTestUtils.setField(expiredProvider, "jwtSecret", "404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970");
        ReflectionTestUtils.setField(expiredProvider, "jwtExpirationMs", -1000L); // expired
        expiredProvider.init();

        User user = User.builder()
                .id(2L)
                .email("expired@picanounon.com")
                .name("Expired User")
                .role(Role.ROLE_USER)
                .build();

        String token = expiredProvider.createToken(user);
        assertFalse(jwtTokenProvider.validateToken(token));
    }
}