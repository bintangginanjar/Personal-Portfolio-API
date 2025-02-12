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

import com.api.rest.portfolio.entity.ProfileEntity;
import com.api.rest.portfolio.entity.RoleEntity;
import com.api.rest.portfolio.entity.UserEntity;
import com.api.rest.portfolio.model.ProfileResponse;
import com.api.rest.portfolio.model.RegisterProfileRequest;
import com.api.rest.portfolio.model.UpdateProfileRequest;
import com.api.rest.portfolio.model.WebResponse;
import com.api.rest.portfolio.repository.ProfileRepository;
import com.api.rest.portfolio.repository.RoleRepository;
import com.api.rest.portfolio.repository.UserRepository;
import com.api.rest.portfolio.security.JwtUtil;
import com.api.rest.portfolio.security.SecurityConstants;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

@EnableWebMvc
@SpringBootTest
@AutoConfigureMockMvc
public class ProfileControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProfileRepository profileRepository;

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

    private final String firstname = "firstname";
    private final String lastname = "lastname";
    private final String aboutMe = "about me";

    @BeforeEach
    void setUp() {                

        profileRepository.deleteAll();
        userRepository.deleteAll();

        RoleEntity role = roleRepository.findByName("ROLE_ADMIN").orElse(null);

        UserEntity user = new UserEntity();
        user.setUsername(username);        
        user.setPassword(passwordEncoder.encode(password));
        user.setRoles(Collections.singletonList(role));        
        userRepository.save(user);
    }

    @Test
    void testCreateProfileSuccess() throws Exception {
        UserEntity user = userRepository.findByUsername(username).orElse(null);

        RegisterProfileRequest request = new RegisterProfileRequest();
        request.setFirstname(firstname);
        request.setLastname(lastname);
        request.setAbout(aboutMe);

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
                post("/api/users/profiles")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .header("Authorization", mockBearerToken)                        
        ).andExpectAll(
                status().isOk()
        ).andDo(result -> {
                WebResponse<ProfileResponse> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });

            assertEquals(true, response.getStatus());
        });
    }

    @Test
    void testCreateProfileBlank() throws Exception {
        UserEntity user = userRepository.findByUsername(username).orElse(null);

        RegisterProfileRequest request = new RegisterProfileRequest();
        request.setFirstname("");
        request.setLastname("");
        request.setAbout(aboutMe);

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
                post("/api/users/profiles")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .header("Authorization", mockBearerToken)                        
        ).andExpectAll(
                status().isBadRequest()
        ).andDo(result -> {
                WebResponse<ProfileResponse> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });

            assertEquals(false, response.getStatus());
        });
    }

    @Test
    void testCreateProfileInvalidToken() throws Exception {
        UserEntity user = userRepository.findByUsername(username).orElse(null);

        RegisterProfileRequest request = new RegisterProfileRequest();
        request.setFirstname(firstname);
        request.setLastname(lastname);
        request.setAbout(aboutMe);

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
                post("/api/users/profiles")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .header("Authorization", mockBearerToken)                        
        ).andExpectAll(
                status().isUnauthorized()
        ).andDo(result -> {
                WebResponse<ProfileResponse> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });

            assertEquals(false, response.getStatus());
        });
    }

    @Test
    void testCreateProfileTokenExpired() throws Exception {
        UserEntity user = userRepository.findByUsername(username).orElse(null);

        RegisterProfileRequest request = new RegisterProfileRequest();
        request.setFirstname(firstname);
        request.setLastname(lastname);
        request.setAbout(aboutMe);

        Authentication authentication = authenticationManager.authenticate(
                                            new UsernamePasswordAuthenticationToken(
                                                username, password)
                                            );

        String mockToken = jwtUtil.generateToken(authentication);

        user.setToken(mockToken);
        user.setTokenExpiredAt(System.currentTimeMillis() - SecurityConstants.JWTexpiration);
        userRepository.save(user);

        String mockBearerToken = "Bearer " + mockToken;

        mockMvc.perform(
                post("/api/users/profiles")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .header("Authorization", mockBearerToken)                        
        ).andExpectAll(
                status().isForbidden()
        ).andDo(result -> {
                WebResponse<ProfileResponse> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });

            assertEquals(false, response.getStatus());
        });
    }

    @Test
    void testCreateProfileNoToken() throws Exception {
        UserEntity user = userRepository.findByUsername(username).orElse(null);

        RegisterProfileRequest request = new RegisterProfileRequest();
        request.setFirstname(firstname);
        request.setLastname(lastname);
        request.setAbout(aboutMe);

        Authentication authentication = authenticationManager.authenticate(
                                            new UsernamePasswordAuthenticationToken(
                                                username, password)
                                            );

        String mockToken = jwtUtil.generateToken(authentication);

        user.setToken(mockToken);
        user.setTokenExpiredAt(System.currentTimeMillis() - SecurityConstants.JWTexpiration);
        userRepository.save(user);
        
        mockMvc.perform(
                post("/api/users/profiles")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))                                              
        ).andExpectAll(
                status().isForbidden()
        ).andDo(result -> {
                WebResponse<ProfileResponse> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });

            assertEquals(false, response.getStatus());
        });
    }

    @Test
    void testGetProfileSuccess() throws Exception {        
        UserEntity user = userRepository.findByUsername(username).orElse(null);

        ProfileEntity profile = new ProfileEntity();
        profile.setFirstname(firstname);
        profile.setLastname(lastname);
        profile.setAbout(aboutMe);
        profile.setUserEntity(user);
        profileRepository.save(profile);

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
                get("/api/users/profiles")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)                        
                        .header("Authorization", mockBearerToken)                        
        ).andExpectAll(
                status().isOk()
        ).andDo(result -> {
                WebResponse<ProfileResponse> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });

            assertEquals(true, response.getStatus());
        });
    }

    @Test
    void testGetProfileNotFound() throws Exception {        
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
                get("/api/users/profiles")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)                        
                        .header("Authorization", mockBearerToken)                        
        ).andExpectAll(
                status().isNotFound()
        ).andDo(result -> {
                WebResponse<ProfileResponse> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });

            assertEquals(false, response.getStatus());
        });
    }

    @Test
    void testGetProfileInvalidToken() throws Exception {        
        UserEntity user = userRepository.findByUsername(username).orElse(null);

        ProfileEntity profile = new ProfileEntity();
        profile.setFirstname(firstname);
        profile.setLastname(lastname);
        profile.setAbout(aboutMe);
        profile.setUserEntity(user);
        profileRepository.save(profile);

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
                get("/api/users/profiles")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)                        
                        .header("Authorization", mockBearerToken)                        
        ).andExpectAll(
                status().isUnauthorized()
        ).andDo(result -> {
                WebResponse<ProfileResponse> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });

            assertEquals(false, response.getStatus());
        });
    }

    @Test
    void testGetProfileTokenExpired() throws Exception {        
        UserEntity user = userRepository.findByUsername(username).orElse(null);

        ProfileEntity profile = new ProfileEntity();
        profile.setFirstname(firstname);
        profile.setLastname(lastname);
        profile.setAbout(aboutMe);
        profile.setUserEntity(user);
        profileRepository.save(profile);

        Authentication authentication = authenticationManager.authenticate(
                                            new UsernamePasswordAuthenticationToken(
                                                username, password)
                                            );

        String mockToken = jwtUtil.generateToken(authentication);

        user.setToken(mockToken);
        user.setTokenExpiredAt(System.currentTimeMillis() - SecurityConstants.JWTexpiration);
        userRepository.save(user);

        String mockBearerToken = "Bearer " + mockToken;

        mockMvc.perform(
                get("/api/users/profiles")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)                        
                        .header("Authorization", mockBearerToken)                        
        ).andExpectAll(
                status().isForbidden()
        ).andDo(result -> {
                WebResponse<ProfileResponse> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });

            assertEquals(false, response.getStatus());
        });
    }

    @Test
    void testGetProfileNoToken() throws Exception {        
        UserEntity user = userRepository.findByUsername(username).orElse(null);

        ProfileEntity profile = new ProfileEntity();
        profile.setFirstname(firstname);
        profile.setLastname(lastname);
        profile.setAbout(aboutMe);
        profile.setUserEntity(user);
        profileRepository.save(profile);

        Authentication authentication = authenticationManager.authenticate(
                                            new UsernamePasswordAuthenticationToken(
                                                username, password)
                                            );

        String mockToken = jwtUtil.generateToken(authentication);

        user.setToken(mockToken);
        user.setTokenExpiredAt(System.currentTimeMillis() + SecurityConstants.JWTexpiration);
        userRepository.save(user);        

        mockMvc.perform(
                get("/api/users/profiles")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)                                                                  
        ).andExpectAll(
                status().isForbidden()
        ).andDo(result -> {
                WebResponse<ProfileResponse> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });

            assertEquals(false, response.getStatus());
        });
    }

    @Test
    void testUpdateProfileSuccess() throws Exception {        
        UserEntity user = userRepository.findByUsername(username).orElse(null);

        ProfileEntity profile = new ProfileEntity();
        profile.setFirstname(firstname);
        profile.setLastname(lastname);
        profile.setAbout(aboutMe);
        profile.setUserEntity(user);
        profileRepository.save(profile);

        UpdateProfileRequest request = new UpdateProfileRequest();
        request.setFirstname("firstname update");
        request.setLastname("lastname update");
        request.setAbout("about me update");

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
                patch("/api/users/profiles")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)  
                        .content(objectMapper.writeValueAsString(request))                      
                        .header("Authorization", mockBearerToken)                        
        ).andExpectAll(
                status().isOk()
        ).andDo(result -> {
                WebResponse<ProfileResponse> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });

            assertEquals(true, response.getStatus());
            assertEquals(request.getFirstname(), response.getData().getFirstname());
            assertEquals(request.getLastname(), response.getData().getLastname());
            assertEquals(request.getAbout(), response.getData().getAbout());
        });
    }

    @Test
    void testUpdateProfileInvalidToken() throws Exception {        
        UserEntity user = userRepository.findByUsername(username).orElse(null);

        ProfileEntity profile = new ProfileEntity();
        profile.setFirstname(firstname);
        profile.setLastname(lastname);
        profile.setAbout(aboutMe);
        profile.setUserEntity(user);
        profileRepository.save(profile);

        UpdateProfileRequest request = new UpdateProfileRequest();
        request.setFirstname("firstname update");
        request.setLastname("lastname update");
        request.setAbout("about me update");

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
                patch("/api/users/profiles")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)  
                        .content(objectMapper.writeValueAsString(request))                      
                        .header("Authorization", mockBearerToken)                        
        ).andExpectAll(
                status().isUnauthorized()
        ).andDo(result -> {
                WebResponse<ProfileResponse> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });

            assertEquals(false, response.getStatus());            
        });
    }

    @Test
    void testUpdateProfileTokenExpired() throws Exception {        
        UserEntity user = userRepository.findByUsername(username).orElse(null);

        ProfileEntity profile = new ProfileEntity();
        profile.setFirstname(firstname);
        profile.setLastname(lastname);
        profile.setAbout(aboutMe);
        profile.setUserEntity(user);
        profileRepository.save(profile);

        UpdateProfileRequest request = new UpdateProfileRequest();
        request.setFirstname("firstname update");
        request.setLastname("lastname update");
        request.setAbout("about me update");

        Authentication authentication = authenticationManager.authenticate(
                                            new UsernamePasswordAuthenticationToken(
                                                username, password)
                                            );

        String mockToken = jwtUtil.generateToken(authentication);

        user.setToken(mockToken);
        user.setTokenExpiredAt(System.currentTimeMillis() - SecurityConstants.JWTexpiration);
        userRepository.save(user);

        String mockBearerToken = "Bearer " + mockToken;

        mockMvc.perform(
                patch("/api/users/profiles")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)  
                        .content(objectMapper.writeValueAsString(request))                      
                        .header("Authorization", mockBearerToken)                        
        ).andExpectAll(
                status().isForbidden()
        ).andDo(result -> {
                WebResponse<ProfileResponse> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });

            assertEquals(false, response.getStatus());            
        });
    }

    @Test
    void testUpdateProfileNoToken() throws Exception {        
        UserEntity user = userRepository.findByUsername(username).orElse(null);

        ProfileEntity profile = new ProfileEntity();
        profile.setFirstname(firstname);
        profile.setLastname(lastname);
        profile.setAbout(aboutMe);
        profile.setUserEntity(user);
        profileRepository.save(profile);

        UpdateProfileRequest request = new UpdateProfileRequest();
        request.setFirstname("firstname update");
        request.setLastname("lastname update");
        request.setAbout("about me update");

        Authentication authentication = authenticationManager.authenticate(
                                            new UsernamePasswordAuthenticationToken(
                                                username, password)
                                            );

        String mockToken = jwtUtil.generateToken(authentication);

        user.setToken(mockToken);
        user.setTokenExpiredAt(System.currentTimeMillis() + SecurityConstants.JWTexpiration);
        userRepository.save(user);        

        mockMvc.perform(
                patch("/api/users/profiles")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)  
                        .content(objectMapper.writeValueAsString(request))                                           
        ).andExpectAll(
                status().isForbidden()
        ).andDo(result -> {
                WebResponse<ProfileResponse> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });

            assertEquals(false, response.getStatus());            
        });
    }

    @Test
    void testUpdateProfileNotFound() throws Exception {        
        UserEntity user = userRepository.findByUsername(username).orElse(null);
      
        UpdateProfileRequest request = new UpdateProfileRequest();
        request.setFirstname("firstname update");
        request.setLastname("lastname update");
        request.setAbout("about me update");

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
                patch("/api/users/profiles")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)  
                        .content(objectMapper.writeValueAsString(request))
                        .header("Authorization", mockBearerToken)                                           
        ).andExpectAll(
                status().isNotFound()
        ).andDo(result -> {
                WebResponse<ProfileResponse> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });

            assertEquals(false, response.getStatus());            
        });
    }
    
}
