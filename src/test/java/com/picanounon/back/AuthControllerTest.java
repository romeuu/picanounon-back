package com.picanounon.back;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.picanounon.back.dto.auth.GoogleTokenRequest;
import com.picanounon.back.dto.auth.LoginRequest;
import com.picanounon.back.dto.auth.RegisterRequest;
import com.picanounon.back.model.AuthProvider;
import com.picanounon.back.model.Role;
import com.picanounon.back.model.User;
import com.picanounon.back.security.CustomUserDetailsService;
import com.picanounon.back.security.jwt.JwtTokenProvider;
import com.picanounon.back.service.GoogleAuthService;
import com.picanounon.back.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private GoogleAuthService googleAuthService;

    @MockBean
    private UserService userService;

    @MockBean
    private JwtTokenProvider jwtTokenProvider;

    @MockBean
    private AuthenticationManager authenticationManager;

    @Test
    void testGoogleLogin_Success() throws Exception {
        GoogleTokenRequest req = new GoogleTokenRequest("valid-google-id-token");
        GoogleIdToken.Payload payload = new GoogleIdToken.Payload();
        payload.setEmail("user@gmail.com");

        User user = User.builder()
                .id(1L)
                .email("user@gmail.com")
                .name("Google User")
                .provider(AuthProvider.GOOGLE)
                .role(Role.ROLE_USER)
                .build();

        when(googleAuthService.verifyToken("valid-google-id-token")).thenReturn(payload);
        when(userService.processOAuthUser(payload)).thenReturn(user);
        when(jwtTokenProvider.createToken(user)).thenReturn("mocked-jwt-token");

        mockMvc.perform(post("/api/auth/google")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.accessToken").value("mocked-jwt-token"))
                .andExpect(jsonPath("$.data.user.email").value("user@gmail.com"));
    }

    @Test
    void testLocalLogin_Success() throws Exception {
        LoginRequest req = new LoginRequest("user@example.com", "secret123");

        User user = User.builder()
                .id(2L)
                .email("user@example.com")
                .name("Local User")
                .provider(AuthProvider.LOCAL)
                .role(Role.ROLE_USER)
                .build();

        UsernamePasswordAuthenticationToken authToken =
                new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authToken);
        when(jwtTokenProvider.createToken(user)).thenReturn("mocked-jwt-token");

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.accessToken").value("mocked-jwt-token"))
                .andExpect(jsonPath("$.data.user.email").value("user@example.com"));
    }

    @Test
    void testRegister_Success() throws Exception {
        RegisterRequest req = new RegisterRequest("newuser@example.com", "password123", "New User");

        User user = User.builder()
                .id(3L)
                .email("newuser@example.com")
                .name("New User")
                .provider(AuthProvider.LOCAL)
                .role(Role.ROLE_USER)
                .build();

        when(userService.registerLocal(any(RegisterRequest.class))).thenReturn(user);
        when(jwtTokenProvider.createToken(user)).thenReturn("mocked-jwt-token");

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.accessToken").value("mocked-jwt-token"))
                .andExpect(jsonPath("$.data.user.email").value("newuser@example.com"));
    }

    @MockBean
    private CustomUserDetailsService userDetailsService;

    @Test
    void testGetMe_UnauthorizedWithoutToken() throws Exception {
        mockMvc.perform(get("/api/auth/me"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void testGetMe_AuthorizedWithToken() throws Exception {
        User user = User.builder()
                .id(1L)
                .email("authuser@picanounon.com")
                .name("Auth User")
                .provider(AuthProvider.GOOGLE)
                .role(Role.ROLE_USER)
                .build();

        when(jwtTokenProvider.validateToken("valid-token")).thenReturn(true);
        when(jwtTokenProvider.getEmailFromToken("valid-token")).thenReturn("authuser@picanounon.com");
        when(userDetailsService.loadUserByUsername("authuser@picanounon.com")).thenReturn(user);

        mockMvc.perform(get("/api/auth/me")
                        .header("Authorization", "Bearer valid-token"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.email").value("authuser@picanounon.com"))
                .andExpect(jsonPath("$.data.name").value("Auth User"));
    }
}