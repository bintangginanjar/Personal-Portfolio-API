package com.api.rest.portfolio.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.Collections;
import java.util.List;

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

import com.api.rest.portfolio.entity.RoleEntity;
import com.api.rest.portfolio.entity.SkillEntity;
import com.api.rest.portfolio.entity.UserEntity;
import com.api.rest.portfolio.model.RegisterSkillRequest;
import com.api.rest.portfolio.model.SkillResponse;
import com.api.rest.portfolio.model.UpdateSkillRequest;
import com.api.rest.portfolio.model.WebResponse;
import com.api.rest.portfolio.repository.RoleRepository;
import com.api.rest.portfolio.repository.SkillRepository;
import com.api.rest.portfolio.repository.UserRepository;
import com.api.rest.portfolio.security.JwtUtil;
import com.api.rest.portfolio.security.SecurityConstants;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

@EnableWebMvc
@SpringBootTest
@AutoConfigureMockMvc
public class SkillControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private SkillRepository skillRepository;

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

    private final String name = "coding";
    private final String imageUrl = "https://cdn-icons-png.flaticon.com/512/4191/4191039.png";
    private final Boolean published = true;

    @BeforeEach
    void setUp() {                

        skillRepository.deleteAll();
        userRepository.deleteAll();

        RoleEntity role = roleRepository.findByName("ROLE_ADMIN").orElse(null);

        UserEntity user = new UserEntity();
        user.setUsername(username);        
        user.setPassword(passwordEncoder.encode(password));
        user.setRoles(Collections.singletonList(role));        
        userRepository.save(user);
    }

    @Test
    void testRegisterSkillSuccess() throws Exception {
        UserEntity user = userRepository.findByUsername(username).orElse(null);

        RegisterSkillRequest request = new RegisterSkillRequest();
        request.setName(name);
        request.setImageUrl(imageUrl);
        request.setIsPublished(published);

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
                post("/api/users/skills")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .header("Authorization", mockBearerToken)                        
        ).andExpectAll(
                status().isOk()
        ).andDo(result -> {
                WebResponse<SkillResponse> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });

            assertEquals(true, response.getStatus());
        });
    }

    @Test
    void testRegisterSkillBlank() throws Exception {
        UserEntity user = userRepository.findByUsername(username).orElse(null);

        RegisterSkillRequest request = new RegisterSkillRequest();
        request.setName("");
        request.setImageUrl("");
        //request.setIsPublished(isPublished);

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
                post("/api/users/skills")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .header("Authorization", mockBearerToken)                        
        ).andExpectAll(
                status().isBadRequest()
        ).andDo(result -> {
                WebResponse<SkillResponse> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });

            assertEquals(false, response.getStatus());
        });
    }

    @Test
    void testRegisterSkillInvalidToken() throws Exception {
        UserEntity user = userRepository.findByUsername(username).orElse(null);

        RegisterSkillRequest request = new RegisterSkillRequest();
        request.setName(name);
        request.setImageUrl(imageUrl);
        //request.setIsPublished(isPublished);

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
                post("/api/users/skills")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .header("Authorization", mockBearerToken)                        
        ).andExpectAll(
                status().isUnauthorized()
        ).andDo(result -> {
                WebResponse<SkillResponse> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });

            assertEquals(false, response.getStatus());
        });
    }

    @Test
    void testRegisterSkillTokenExpired() throws Exception {
        UserEntity user = userRepository.findByUsername(username).orElse(null);

        RegisterSkillRequest request = new RegisterSkillRequest();
        request.setName(name);
        request.setImageUrl(imageUrl);
        //request.setIsPublished(isPublished);

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
                post("/api/users/skills")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .header("Authorization", mockBearerToken)                        
        ).andExpectAll(
                status().isForbidden()
        ).andDo(result -> {
                WebResponse<SkillResponse> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });

            assertEquals(false, response.getStatus());
        });
    }

    @Test
    void testRegisterSkillNoToken() throws Exception {
        UserEntity user = userRepository.findByUsername(username).orElse(null);

        RegisterSkillRequest request = new RegisterSkillRequest();
        request.setName(name);
        request.setImageUrl(imageUrl);
        //request.setIsPublished(isPublished);

        Authentication authentication = authenticationManager.authenticate(
                                            new UsernamePasswordAuthenticationToken(
                                                username, password)
                                            );

        String mockToken = jwtUtil.generateToken(authentication);

        user.setToken(mockToken);
        user.setTokenExpiredAt(System.currentTimeMillis() - SecurityConstants.JWTexpiration);
        userRepository.save(user);

        mockMvc.perform(
                post("/api/users/skills")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))                                            
        ).andExpectAll(
                status().isForbidden()
        ).andDo(result -> {
                WebResponse<SkillResponse> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });

            assertEquals(false, response.getStatus());
        });
    }

    @Test
    void testGetSkillSuccess() throws Exception {
        UserEntity user = userRepository.findByUsername(username).orElse(null);

        SkillEntity skill = new SkillEntity();
        skill.setName(name);
        skill.setImageUrl(imageUrl);        
        skill.setIsPublished(published);
        skill.setUserEntity(user);
        skillRepository.save(skill);

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
                get("/api/users/skills/" + skill.getId())
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)                        
                        .header("Authorization", mockBearerToken)                        
        ).andExpectAll(
                status().isOk()
        ).andDo(result -> {
                WebResponse<SkillResponse> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });

            assertEquals(true, response.getStatus());
            assertEquals(skill.getName(), response.getData().getName());
        });
    }

    @Test
    void testGetSkillBadId() throws Exception {
        UserEntity user = userRepository.findByUsername(username).orElse(null);

        SkillEntity skill = new SkillEntity();
        skill.setName(name);
        skill.setImageUrl(imageUrl);        
        skill.setIsPublished(published);
        skill.setUserEntity(user);
        skillRepository.save(skill);

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
                get("/api/users/skills/" + skill.getId() + "a")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)                        
                        .header("Authorization", mockBearerToken)                        
        ).andExpectAll(
                status().isBadRequest()
        ).andDo(result -> {
                WebResponse<SkillResponse> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });

            assertEquals(false, response.getStatus());
        });
    }

    @Test
    void testGetSkillNotFound() throws Exception {
        UserEntity user = userRepository.findByUsername(username).orElse(null);

        SkillEntity skill = new SkillEntity();
        skill.setName(name);
        skill.setImageUrl(imageUrl);        
        skill.setIsPublished(published);
        skill.setUserEntity(user);
        skillRepository.save(skill);

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
                get("/api/users/skills/1")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)                        
                        .header("Authorization", mockBearerToken)                        
        ).andExpectAll(
                status().isNotFound()
        ).andDo(result -> {
                WebResponse<SkillResponse> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });

            assertEquals(false, response.getStatus());
        });
    }

    @Test
    void testGetSkillInvalidToken() throws Exception {
        UserEntity user = userRepository.findByUsername(username).orElse(null);

        SkillEntity skill = new SkillEntity();
        skill.setName(name);
        skill.setImageUrl(imageUrl);        
        skill.setIsPublished(published);
        skill.setUserEntity(user);
        skillRepository.save(skill);

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
                get("/api/users/skills/" + skill.getId())
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)                        
                        .header("Authorization", mockBearerToken)                        
        ).andExpectAll(
                status().isUnauthorized()
        ).andDo(result -> {
                WebResponse<SkillResponse> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });

            assertEquals(false, response.getStatus());
        });
    }

    @Test
    void testGetSkillTokenExpired() throws Exception {
        UserEntity user = userRepository.findByUsername(username).orElse(null);

        SkillEntity skill = new SkillEntity();
        skill.setName(name);
        skill.setImageUrl(imageUrl);        
        skill.setIsPublished(published);
        skill.setUserEntity(user);
        skillRepository.save(skill);

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
                get("/api/users/skills/" + skill.getId())
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)                        
                        .header("Authorization", mockBearerToken)                        
        ).andExpectAll(
                status().isForbidden()
        ).andDo(result -> {
                WebResponse<SkillResponse> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });

            assertEquals(false, response.getStatus());
        });
    }

    @Test
    void testGetSkillNoToken() throws Exception {
        UserEntity user = userRepository.findByUsername(username).orElse(null);

        SkillEntity skill = new SkillEntity();
        skill.setName(name);
        skill.setImageUrl(imageUrl);        
        skill.setIsPublished(published);
        skill.setUserEntity(user);
        skillRepository.save(skill);

        Authentication authentication = authenticationManager.authenticate(
                                            new UsernamePasswordAuthenticationToken(
                                                username, password)
                                            );

        String mockToken = jwtUtil.generateToken(authentication);

        user.setToken(mockToken);
        user.setTokenExpiredAt(System.currentTimeMillis() + SecurityConstants.JWTexpiration);
        userRepository.save(user);

        mockMvc.perform(
                get("/api/users/skills/" + skill.getId())
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)                                                                      
        ).andExpectAll(
                status().isForbidden()
        ).andDo(result -> {
                WebResponse<SkillResponse> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });

            assertEquals(false, response.getStatus());
        });
    }

    @Test
    void testUpdateSkillSuccess() throws Exception {
        UserEntity user = userRepository.findByUsername(username).orElse(null);

        SkillEntity skill = new SkillEntity();
        skill.setName(name);
        skill.setImageUrl(imageUrl);        
        skill.setIsPublished(published);
        skill.setUserEntity(user);
        skillRepository.save(skill);

        UpdateSkillRequest request = new UpdateSkillRequest();
        request.setName("updated skill");
        request.setImageUrl("https://static.thenounproject.com/png/883149-200.png");
        request.setIsPublished(false);

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
                patch("/api/users/skills/" + skill.getId())
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .header("Authorization", mockBearerToken)                        
        ).andExpectAll(
                status().isOk()
        ).andDo(result -> {
                WebResponse<SkillResponse> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });

            assertEquals(true, response.getStatus());
            assertEquals(request.getName(), response.getData().getName());
            assertEquals(request.getImageUrl(), response.getData().getImageUrl());
            assertEquals(request.getIsPublished(), response.getData().getIsPublished());
        });
    }

    @Test
    void testUpdateSkillNotFound() throws Exception {
        UserEntity user = userRepository.findByUsername(username).orElse(null);

        SkillEntity skill = new SkillEntity();
        skill.setName(name);
        skill.setImageUrl(imageUrl);        
        skill.setIsPublished(published);
        skill.setUserEntity(user);
        skillRepository.save(skill);

        UpdateSkillRequest request = new UpdateSkillRequest();
        request.setName("updated skill");
        request.setImageUrl("https://static.thenounproject.com/png/883149-200.png");
        request.setIsPublished(false);

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
                patch("/api/users/skills/1")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .header("Authorization", mockBearerToken)                        
        ).andExpectAll(
                status().isNotFound()
        ).andDo(result -> {
                WebResponse<SkillResponse> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });

            assertEquals(false, response.getStatus());            
        });
    }

    @Test
    void testUpdateSkillBadId() throws Exception {
        UserEntity user = userRepository.findByUsername(username).orElse(null);

        SkillEntity skill = new SkillEntity();
        skill.setName(name);
        skill.setImageUrl(imageUrl);        
        skill.setIsPublished(published);
        skill.setUserEntity(user);
        skillRepository.save(skill);

        UpdateSkillRequest request = new UpdateSkillRequest();
        request.setName("updated skill");
        request.setImageUrl("https://static.thenounproject.com/png/883149-200.png");
        request.setIsPublished(false);

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
                patch("/api/users/skills/" + skill.getId() + "a")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .header("Authorization", mockBearerToken)                        
        ).andExpectAll(
                status().isBadRequest()
        ).andDo(result -> {
                WebResponse<SkillResponse> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });

            assertEquals(false, response.getStatus());            
        });
    }

    @Test
    void testUpdateSkillInvalidToken() throws Exception {
        UserEntity user = userRepository.findByUsername(username).orElse(null);

        SkillEntity skill = new SkillEntity();
        skill.setName(name);
        skill.setImageUrl(imageUrl);        
        skill.setIsPublished(published);
        skill.setUserEntity(user);
        skillRepository.save(skill);

        UpdateSkillRequest request = new UpdateSkillRequest();
        request.setName("updated skill");
        request.setImageUrl("https://static.thenounproject.com/png/883149-200.png");
        request.setIsPublished(false);

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
                patch("/api/users/skills/" + skill.getId())
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .header("Authorization", mockBearerToken)                        
        ).andExpectAll(
                status().isUnauthorized()
        ).andDo(result -> {
                WebResponse<SkillResponse> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });

            assertEquals(false, response.getStatus());            
        });
    }

    @Test
    void testUpdateSkillTokenExpired() throws Exception {
        UserEntity user = userRepository.findByUsername(username).orElse(null);

        SkillEntity skill = new SkillEntity();
        skill.setName(name);
        skill.setImageUrl(imageUrl);        
        skill.setIsPublished(published);
        skill.setUserEntity(user);
        skillRepository.save(skill);

        UpdateSkillRequest request = new UpdateSkillRequest();
        request.setName("updated skill");
        request.setImageUrl("https://static.thenounproject.com/png/883149-200.png");
        request.setIsPublished(false);

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
                patch("/api/users/skills/" + skill.getId())
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .header("Authorization", mockBearerToken)                        
        ).andExpectAll(
                status().isForbidden()
        ).andDo(result -> {
                WebResponse<SkillResponse> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });

            assertEquals(false, response.getStatus());            
        });
    }

    @Test
    void testUpdateSkillNoToken() throws Exception {
        UserEntity user = userRepository.findByUsername(username).orElse(null);

        SkillEntity skill = new SkillEntity();
        skill.setName(name);
        skill.setImageUrl(imageUrl);        
        skill.setIsPublished(published);
        skill.setUserEntity(user);
        skillRepository.save(skill);

        UpdateSkillRequest request = new UpdateSkillRequest();
        request.setName("updated skill");
        request.setImageUrl("https://static.thenounproject.com/png/883149-200.png");
        request.setIsPublished(false);

        Authentication authentication = authenticationManager.authenticate(
                                            new UsernamePasswordAuthenticationToken(
                                                username, password)
                                            );

        String mockToken = jwtUtil.generateToken(authentication);

        user.setToken(mockToken);
        user.setTokenExpiredAt(System.currentTimeMillis() + SecurityConstants.JWTexpiration);
        userRepository.save(user);    

        mockMvc.perform(
                patch("/api/users/skills/" + skill.getId())
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))                                            
        ).andExpectAll(
                status().isForbidden()
        ).andDo(result -> {
                WebResponse<SkillResponse> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });

            assertEquals(false, response.getStatus());            
        });
    }

    @Test
    void testDeleteSkillSuccess() throws Exception {
        UserEntity user = userRepository.findByUsername(username).orElse(null);

        SkillEntity skill = new SkillEntity();
        skill.setName(name);
        skill.setImageUrl(imageUrl);        
        skill.setIsPublished(published);
        skill.setUserEntity(user);
        skillRepository.save(skill);

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
                delete("/api/users/skills/" + skill.getId())
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)                        
                        .header("Authorization", mockBearerToken)                        
        ).andExpectAll(
                status().isOk()
        ).andDo(result -> {
                WebResponse<String> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });

            assertEquals(true, response.getStatus());            
        });
    }

    @Test
    void testDeleteNotFound() throws Exception {
        UserEntity user = userRepository.findByUsername(username).orElse(null);

        SkillEntity skill = new SkillEntity();
        skill.setName(name);
        skill.setImageUrl(imageUrl);        
        skill.setIsPublished(published);
        skill.setUserEntity(user);
        skillRepository.save(skill);

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
                delete("/api/users/skills/999")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)                        
                        .header("Authorization", mockBearerToken)                        
        ).andExpectAll(
                status().isNotFound()
        ).andDo(result -> {
                WebResponse<String> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });

            assertEquals(false, response.getStatus());            
        });
    }

    @Test
    void testDeleteBadId() throws Exception {
        UserEntity user = userRepository.findByUsername(username).orElse(null);

        SkillEntity skill = new SkillEntity();
        skill.setName(name);
        skill.setImageUrl(imageUrl);        
        skill.setIsPublished(published);
        skill.setUserEntity(user);
        skillRepository.save(skill);

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
                delete("/api/users/skills/" + user.getId() + "a")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)                        
                        .header("Authorization", mockBearerToken)                        
        ).andExpectAll(
                status().isBadRequest()
        ).andDo(result -> {
                WebResponse<String> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });

            assertEquals(false, response.getStatus());            
        });
    }

    @Test
    void testDeleteInvalidToken() throws Exception {
        UserEntity user = userRepository.findByUsername(username).orElse(null);

        SkillEntity skill = new SkillEntity();
        skill.setName(name);
        skill.setImageUrl(imageUrl);        
        skill.setIsPublished(published);
        skill.setUserEntity(user);
        skillRepository.save(skill);

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
                delete("/api/users/skills/" + skill.getId())
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)                        
                        .header("Authorization", mockBearerToken)                        
        ).andExpectAll(
                status().isUnauthorized()
        ).andDo(result -> {
                WebResponse<String> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });

            assertEquals(false, response.getStatus());            
        });
    }

    @Test
    void testDeleteTokenExpired() throws Exception {
        UserEntity user = userRepository.findByUsername(username).orElse(null);

        SkillEntity skill = new SkillEntity();
        skill.setName(name);
        skill.setImageUrl(imageUrl);        
        skill.setIsPublished(published);
        skill.setUserEntity(user);
        skillRepository.save(skill);

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
                delete("/api/users/skills/" + skill.getId())
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)                        
                        .header("Authorization", mockBearerToken)                        
        ).andExpectAll(
                status().isForbidden()
        ).andDo(result -> {
                WebResponse<String> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });

            assertEquals(false, response.getStatus());            
        });
    }

    @Test
    void testDeleteNoToken() throws Exception {
        UserEntity user = userRepository.findByUsername(username).orElse(null);

        SkillEntity skill = new SkillEntity();
        skill.setName(name);
        skill.setImageUrl(imageUrl);        
        skill.setIsPublished(published);
        skill.setUserEntity(user);
        skillRepository.save(skill);

        Authentication authentication = authenticationManager.authenticate(
                                            new UsernamePasswordAuthenticationToken(
                                                username, password)
                                            );

        String mockToken = jwtUtil.generateToken(authentication);

        user.setToken(mockToken);
        user.setTokenExpiredAt(System.currentTimeMillis() + SecurityConstants.JWTexpiration);
        userRepository.save(user);        

        mockMvc.perform(
                delete("/api/users/skills/" + skill.getId())
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)                                                                       
        ).andExpectAll(
                status().isForbidden()
        ).andDo(result -> {
                WebResponse<String> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });

            assertEquals(false, response.getStatus());            
        });
    }

    @Test
    void testGetSkillListSuccess() throws Exception {
        UserEntity user = userRepository.findByUsername(username).orElse(null);

        SkillEntity skill = new SkillEntity();
        skill.setName(name);
        skill.setImageUrl(imageUrl);        
        skill.setIsPublished(published);
        skill.setUserEntity(user);
        skillRepository.save(skill);

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
                get("/api/users/skills/list")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)                        
                        .header("Authorization", mockBearerToken)                        
        ).andExpectAll(
                status().isOk()
        ).andDo(result -> {
                WebResponse<List<SkillResponse>> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });

            assertEquals(true, response.getStatus());            
        });
    }

    @Test
    void testGetSkillListInvalidToken() throws Exception {
        UserEntity user = userRepository.findByUsername(username).orElse(null);

        SkillEntity skill = new SkillEntity();
        skill.setName(name);
        skill.setImageUrl(imageUrl);        
        skill.setIsPublished(published);
        skill.setUserEntity(user);
        skillRepository.save(skill);

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
                get("/api/users/skills/list")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)                        
                        .header("Authorization", mockBearerToken)                        
        ).andExpectAll(
                status().isUnauthorized()
        ).andDo(result -> {
                WebResponse<List<SkillResponse>> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });

            assertEquals(false, response.getStatus());            
        });
    }

    @Test
    void testGetSkillListTokenExpired() throws Exception {
        UserEntity user = userRepository.findByUsername(username).orElse(null);

        SkillEntity skill = new SkillEntity();
        skill.setName(name);
        skill.setImageUrl(imageUrl);        
        skill.setIsPublished(published);
        skill.setUserEntity(user);
        skillRepository.save(skill);

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
                get("/api/users/skills/list")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)                        
                        .header("Authorization", mockBearerToken)                        
        ).andExpectAll(
                status().isForbidden()
        ).andDo(result -> {
                WebResponse<List<SkillResponse>> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });

            assertEquals(false, response.getStatus());            
        });
    }

    @Test
    void testGetSkillListNoToken() throws Exception {
        UserEntity user = userRepository.findByUsername(username).orElse(null);

        SkillEntity skill = new SkillEntity();
        skill.setName(name);
        skill.setImageUrl(imageUrl);        
        skill.setIsPublished(published);
        skill.setUserEntity(user);
        skillRepository.save(skill);

        Authentication authentication = authenticationManager.authenticate(
                                            new UsernamePasswordAuthenticationToken(
                                                username, password)
                                            );

        String mockToken = jwtUtil.generateToken(authentication);

        user.setToken(mockToken);
        user.setTokenExpiredAt(System.currentTimeMillis() - SecurityConstants.JWTexpiration);
        userRepository.save(user);        

        mockMvc.perform(
                get("/api/users/skills/list")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)                                                                     
        ).andExpectAll(
                status().isForbidden()
        ).andDo(result -> {
                WebResponse<List<SkillResponse>> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });

            assertEquals(false, response.getStatus());            
        });
    }
}
