package com.api.rest.portfolio.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.Collections;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import com.fasterxml.jackson.core.type.TypeReference;
import com.api.rest.portfolio.entity.RoleEntity;
import com.api.rest.portfolio.entity.UserEntity;
import com.api.rest.portfolio.model.LoginUserRequest;
import com.api.rest.portfolio.model.TokenResponse;
import com.api.rest.portfolio.model.WebResponse;
import com.api.rest.portfolio.repository.RoleRepository;
import com.api.rest.portfolio.repository.UserRepository;
import com.api.rest.portfolio.security.JwtUtil;
import com.api.rest.portfolio.security.SecurityConstants;
import com.fasterxml.jackson.databind.ObjectMapper;

@EnableWebMvc
@SpringBootTest
@AutoConfigureMockMvc
public class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private ObjectMapper objectMapper;

    private final String username = "test";
    private final String password = "rahasia";

    @BeforeEach
    void setUp() {                

        userRepository.deleteAll();

        RoleEntity role = roleRepository.findByName("ROLE_ADMIN").orElse(null);

        UserEntity user = new UserEntity();
        user.setUsername(username);        
        user.setPassword(passwordEncoder.encode(password));
        user.setRoles(Collections.singletonList(role));        
        userRepository.save(user);

    }

    @Test
    void testLoginSuccess() throws Exception {
        LoginUserRequest request = new LoginUserRequest();
        request.setUsername(username);
        request.setPassword(password);        

        mockMvc.perform(
                post("/api/auth/login")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))                        
        ).andExpectAll(
                status().isOk()
        ).andDo(result -> {
                WebResponse<TokenResponse> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });

            assertEquals(true, response.getStatus());
        });
    }

    @Test
    void testLoginFailed() throws Exception {
        LoginUserRequest request = new LoginUserRequest();
        request.setUsername("testuser");
        request.setPassword("testuser");        

        mockMvc.perform(
                post("/api/auth/login")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))                        
        ).andExpectAll(
                status().isUnauthorized()
        ).andDo(result -> {
                WebResponse<TokenResponse> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });

            assertEquals(false, response.getStatus());
        });
    }

    @Test
    void testLogoutSuccess() throws Exception {
        UserEntity user = userRepository.findByUsername(username).orElse(null);
        
        Authentication authentication = authenticationManager.authenticate(
                                            new UsernamePasswordAuthenticationToken(
                                                username, password)
                                            );

        String mockToken = jwtUtil.generateToken(authentication);

        user.setToken(mockToken);
        user.setTokenExpiredAt(System.currentTimeMillis() + SecurityConstants.JWTexpiration);
        userRepository.save(user);

        String mockBearerToken = "Bearer " + mockToken;
        
        mockMvc.perform(
            delete("/api/auth/logout")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)   
                        .header("Authorization", mockBearerToken)                                            
        ).andExpectAll(
                status().isOk()
        ).andDo(result -> {
                WebResponse<TokenResponse> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });

            assertEquals(true, response.getStatus());
        });
    }

    @Test
    void testLogoutInvalidToken() throws Exception {
        UserEntity user = userRepository.findByUsername(username).orElse(null);
        
        Authentication authentication = authenticationManager.authenticate(
                                            new UsernamePasswordAuthenticationToken(
                                                username, password)
                                            );

        String mockToken = jwtUtil.generateToken(authentication);

        user.setToken(mockToken);
        user.setTokenExpiredAt(System.currentTimeMillis() + SecurityConstants.JWTexpiration);
        userRepository.save(user);

        String mockBearerToken = "Bearer " + mockToken + "a";
        
        mockMvc.perform(
            delete("/api/auth/logout")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)   
                        .header("Authorization", mockBearerToken)                                            
        ).andExpectAll(
                status().isUnauthorized()
        ).andDo(result -> {
                WebResponse<TokenResponse> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });

            assertEquals(false, response.getStatus());
        });
    }

    @Test
    void testLogoutNoToken() throws Exception {
        UserEntity user = userRepository.findByUsername(username).orElse(null);
        
        Authentication authentication = authenticationManager.authenticate(
                                            new UsernamePasswordAuthenticationToken(
                                                username, password)
                                            );

        String mockToken = jwtUtil.generateToken(authentication);

        user.setToken(mockToken);
        user.setTokenExpiredAt(System.currentTimeMillis() + SecurityConstants.JWTexpiration);
        userRepository.save(user);        
        
        mockMvc.perform(
            delete("/api/auth/logout")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)                           
        ).andExpectAll(
                status().isForbidden()
        ).andDo(result -> {
                WebResponse<TokenResponse> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });

            assertEquals(false, response.getStatus());
        });
    }
}
