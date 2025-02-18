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

import com.api.rest.portfolio.entity.ProjectEntity;
import com.api.rest.portfolio.entity.RoleEntity;
import com.api.rest.portfolio.entity.UserEntity;
import com.api.rest.portfolio.model.ProjectResponse;
import com.api.rest.portfolio.model.RegisterProjectRequest;
import com.api.rest.portfolio.model.UpdateProjectRequest;
import com.api.rest.portfolio.model.WebResponse;
import com.api.rest.portfolio.repository.ProjectRepository;
import com.api.rest.portfolio.repository.RoleRepository;
import com.api.rest.portfolio.repository.UserRepository;
import com.api.rest.portfolio.security.JwtUtil;
import com.api.rest.portfolio.security.SecurityConstants;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

@EnableWebMvc
@SpringBootTest
@AutoConfigureMockMvc
public class ProjectControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private ProjectRepository projectRepository;

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

    private final String name = "Test Project";
    private final String imageUrl = "https://cdn-icons-png.flaticon.com/512/4191/4191039.png";
    private final String projectUrl = "https://cdn-icons-png.flaticon.com/512/4191/4191039.png";
    private final String description = "Project description";
    private final String hashtag = "#project, #springboot, #backend";
    private final Boolean isPublish = true;
    private final Boolean isOpen = true;

    @BeforeEach
    void setUp() {                

        projectRepository.deleteAll();
        userRepository.deleteAll();

        RoleEntity role = roleRepository.findByName("ROLE_ADMIN").orElse(null);

        UserEntity user = new UserEntity();
        user.setUsername(username);        
        user.setPassword(passwordEncoder.encode(password));
        user.setRoles(Collections.singletonList(role));        
        userRepository.save(user);
    }

    @Test
    void testRegisterProjectSuccess() throws Exception {
        UserEntity user = userRepository.findByUsername(username).orElse(null);

        RegisterProjectRequest request = new RegisterProjectRequest();
        request.setName(name);
        request.setImageUrl(imageUrl);
        request.setUrl(projectUrl);
        request.setDescription(description);
        request.setHashtag(hashtag);
        request.setIsPublished(isPublish);
        request.setIsOpen(isOpen);

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
                post("/api/users/projects")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .header("Authorization", mockBearerToken)                        
        ).andExpectAll(
                status().isOk()
        ).andDo(result -> {
                WebResponse<ProjectResponse> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });

            assertEquals(true, response.getStatus());
        });
    }

    @Test
    void testRegisterProjectBlank() throws Exception {
        UserEntity user = userRepository.findByUsername(username).orElse(null);

        RegisterProjectRequest request = new RegisterProjectRequest();
        request.setName("");
        request.setImageUrl("");
        request.setUrl(projectUrl);
        request.setDescription(description);
        request.setHashtag(hashtag);
        request.setIsPublished(null);
        request.setIsOpen(null);

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
                post("/api/users/projects")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .header("Authorization", mockBearerToken)                        
        ).andExpectAll(
                status().isBadRequest()
        ).andDo(result -> {
                WebResponse<ProjectResponse> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });

            assertEquals(false, response.getStatus());
        });
    }

    @Test
    void testRegisterProjectInvalidToken() throws Exception {
        UserEntity user = userRepository.findByUsername(username).orElse(null);

        RegisterProjectRequest request = new RegisterProjectRequest();
        request.setName("");
        request.setImageUrl("");
        request.setUrl(projectUrl);
        request.setDescription(description);
        request.setHashtag(hashtag);
        request.setIsPublished(null);
        request.setIsOpen(null);

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
                post("/api/users/projects")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .header("Authorization", mockBearerToken)                        
        ).andExpectAll(
                status().isUnauthorized()
        ).andDo(result -> {
                WebResponse<ProjectResponse> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });

            assertEquals(false, response.getStatus());
        });
    }

    @Test
    void testRegisterProjectTokenExpired() throws Exception {
        UserEntity user = userRepository.findByUsername(username).orElse(null);

        RegisterProjectRequest request = new RegisterProjectRequest();
        request.setName("");
        request.setImageUrl("");
        request.setUrl(projectUrl);
        request.setDescription(description);
        request.setHashtag(hashtag);
        request.setIsPublished(null);
        request.setIsOpen(null);

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
                post("/api/users/projects")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .header("Authorization", mockBearerToken)                        
        ).andExpectAll(
                status().isForbidden()
        ).andDo(result -> {
                WebResponse<ProjectResponse> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });

            assertEquals(false, response.getStatus());
        });
    }

    @Test
    void testRegisterProjectNoToken() throws Exception {
        UserEntity user = userRepository.findByUsername(username).orElse(null);

        RegisterProjectRequest request = new RegisterProjectRequest();
        request.setName("");
        request.setImageUrl("");
        request.setUrl(projectUrl);
        request.setDescription(description);
        request.setHashtag(hashtag);
        request.setIsPublished(null);
        request.setIsOpen(null);

        Authentication authentication = authenticationManager.authenticate(
                                            new UsernamePasswordAuthenticationToken(
                                                username, password)
                                            );

        String mockToken = jwtUtil.generateToken(authentication);

        user.setToken(mockToken);
        user.setTokenExpiredAt(System.currentTimeMillis() + SecurityConstants.JWTexpiration);
        userRepository.save(user);        

        mockMvc.perform(
                post("/api/users/projects")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))                                            
        ).andExpectAll(
                status().isForbidden()
        ).andDo(result -> {
                WebResponse<ProjectResponse> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });

            assertEquals(false, response.getStatus());
        });
    }

    @Test
    void testGetProjectSuccess() throws Exception {
        UserEntity user = userRepository.findByUsername(username).orElse(null);

        ProjectEntity project = new ProjectEntity();
        project.setName(name);
        project.setImageUrl(imageUrl);
        project.setUrl(projectUrl);
        project.setDescription(description);
        project.setHashtag(hashtag);
        project.setIsPublished(isPublish);
        project.setIsOpen(isOpen);
        project.setUserEntity(user);
        projectRepository.save(project);

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
                get("/api/users/projects/" + project.getId())
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)                        
                        .header("Authorization", mockBearerToken)                        
        ).andExpectAll(
                status().isOk()
        ).andDo(result -> {
                WebResponse<ProjectResponse> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });

            assertEquals(true, response.getStatus());
        });
    }

    @Test
    void testGetProjectNotFound() throws Exception {
        UserEntity user = userRepository.findByUsername(username).orElse(null);

        ProjectEntity project = new ProjectEntity();
        project.setName(name);
        project.setImageUrl(imageUrl);
        project.setUrl(projectUrl);
        project.setDescription(description);
        project.setHashtag(hashtag);
        project.setIsPublished(isPublish);
        project.setIsOpen(isOpen);
        project.setUserEntity(user);
        projectRepository.save(project);

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
                get("/api/users/projects/1234")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)                        
                        .header("Authorization", mockBearerToken)                        
        ).andExpectAll(
                status().isNotFound()
        ).andDo(result -> {
                WebResponse<ProjectResponse> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });

            assertEquals(false, response.getStatus());
        });
    }

    @Test
    void testGetProjectBadId() throws Exception {
        UserEntity user = userRepository.findByUsername(username).orElse(null);

        ProjectEntity project = new ProjectEntity();
        project.setName(name);
        project.setImageUrl(imageUrl);
        project.setUrl(projectUrl);
        project.setDescription(description);
        project.setHashtag(hashtag);
        project.setIsPublished(isPublish);
        project.setIsOpen(isOpen);
        project.setUserEntity(user);
        projectRepository.save(project);

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
                get("/api/users/projects/" + project.getId() + "a")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)                        
                        .header("Authorization", mockBearerToken)                        
        ).andExpectAll(
                status().isBadRequest()
        ).andDo(result -> {
                WebResponse<ProjectResponse> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });

            assertEquals(false, response.getStatus());
        });
    }

    @Test
    void testGetProjectInvalidToken() throws Exception {
        UserEntity user = userRepository.findByUsername(username).orElse(null);

        ProjectEntity project = new ProjectEntity();
        project.setName(name);
        project.setImageUrl(imageUrl);
        project.setUrl(projectUrl);
        project.setDescription(description);
        project.setHashtag(hashtag);
        project.setIsPublished(isPublish);
        project.setIsOpen(isOpen);
        project.setUserEntity(user);
        projectRepository.save(project);

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
                get("/api/users/projects/" + project.getId())
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)                        
                        .header("Authorization", mockBearerToken)                        
        ).andExpectAll(
                status().isUnauthorized()
        ).andDo(result -> {
                WebResponse<ProjectResponse> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });

            assertEquals(false, response.getStatus());
        });
    }

    @Test
    void testGetProjectTokenExpired() throws Exception {
        UserEntity user = userRepository.findByUsername(username).orElse(null);

        ProjectEntity project = new ProjectEntity();
        project.setName(name);
        project.setImageUrl(imageUrl);
        project.setUrl(projectUrl);
        project.setDescription(description);
        project.setHashtag(hashtag);
        project.setIsPublished(isPublish);
        project.setIsOpen(isOpen);
        project.setUserEntity(user);
        projectRepository.save(project);

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
                get("/api/users/projects/" + project.getId())
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)                        
                        .header("Authorization", mockBearerToken)                        
        ).andExpectAll(
                status().isForbidden()
        ).andDo(result -> {
                WebResponse<ProjectResponse> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });

            assertEquals(false, response.getStatus());
        });
    }

    @Test
    void testGetProjectNoToken() throws Exception {
        UserEntity user = userRepository.findByUsername(username).orElse(null);

        ProjectEntity project = new ProjectEntity();
        project.setName(name);
        project.setImageUrl(imageUrl);
        project.setUrl(projectUrl);
        project.setDescription(description);
        project.setHashtag(hashtag);
        project.setIsPublished(isPublish);
        project.setIsOpen(isOpen);
        project.setUserEntity(user);
        projectRepository.save(project);

        Authentication authentication = authenticationManager.authenticate(
                                            new UsernamePasswordAuthenticationToken(
                                                username, password)
                                            );

        String mockToken = jwtUtil.generateToken(authentication);

        user.setToken(mockToken);
        user.setTokenExpiredAt(System.currentTimeMillis() + SecurityConstants.JWTexpiration);
        userRepository.save(user);

        mockMvc.perform(
                get("/api/users/projects/" + project.getId())
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)                                                                    
        ).andExpectAll(
                status().isForbidden()
        ).andDo(result -> {
                WebResponse<ProjectResponse> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });

            assertEquals(false, response.getStatus());
        });
    }

    @Test
    void testUpdateProjectSuccess() throws Exception {
        UserEntity user = userRepository.findByUsername(username).orElse(null);

        ProjectEntity project = new ProjectEntity();
        project.setName(name);
        project.setImageUrl(imageUrl);
        project.setUrl(projectUrl);
        project.setDescription(description);
        project.setHashtag(hashtag);
        project.setIsPublished(isPublish);
        project.setIsOpen(isOpen);
        project.setUserEntity(user);
        projectRepository.save(project);

        UpdateProjectRequest request = new UpdateProjectRequest();
        request.setName(name + " updated");
        request.setImageUrl(imageUrl);
        request.setUrl(projectUrl);
        request.setDescription(description + " updated");
        request.setHashtag(hashtag);
        request.setIsPublished(false);
        request.setIsOpen(false);

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
                patch("/api/users/projects/" + project.getId())
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .header("Authorization", mockBearerToken)                        
        ).andExpectAll(
                status().isOk()
        ).andDo(result -> {
                WebResponse<ProjectResponse> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });

            assertEquals(true, response.getStatus());
        });
    }

    @Test
    void testUpdateProjectNotFound() throws Exception {
        UserEntity user = userRepository.findByUsername(username).orElse(null);

        ProjectEntity project = new ProjectEntity();
        project.setName(name);
        project.setImageUrl(imageUrl);
        project.setUrl(projectUrl);
        project.setDescription(description);
        project.setHashtag(hashtag);
        project.setIsPublished(isPublish);
        project.setIsOpen(isOpen);
        project.setUserEntity(user);
        projectRepository.save(project);

        UpdateProjectRequest request = new UpdateProjectRequest();
        request.setName(name + " updated");
        request.setImageUrl(imageUrl);
        request.setUrl(projectUrl);
        request.setDescription(description + " updated");
        request.setHashtag(hashtag);
        request.setIsPublished(false);
        request.setIsOpen(false);

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
                patch("/api/users/projects/1")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .header("Authorization", mockBearerToken)                        
        ).andExpectAll(
                status().isNotFound()
        ).andDo(result -> {
                WebResponse<ProjectResponse> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });

            assertEquals(false, response.getStatus());
        });

    }

    @Test
    void testUpdateProjectBadId() throws Exception {
        UserEntity user = userRepository.findByUsername(username).orElse(null);

        ProjectEntity project = new ProjectEntity();
        project.setName(name);
        project.setImageUrl(imageUrl);
        project.setUrl(projectUrl);
        project.setDescription(description);
        project.setHashtag(hashtag);
        project.setIsPublished(isPublish);
        project.setIsOpen(isOpen);
        project.setUserEntity(user);
        projectRepository.save(project);

        UpdateProjectRequest request = new UpdateProjectRequest();
        request.setName(name + " updated");
        request.setImageUrl(imageUrl);
        request.setUrl(projectUrl);
        request.setDescription(description + " updated");
        request.setHashtag(hashtag);
        request.setIsPublished(false);
        request.setIsOpen(false);

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
                patch("/api/users/projects/" + project.getId() + "a")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .header("Authorization", mockBearerToken)                        
        ).andExpectAll(
                status().isBadRequest()
        ).andDo(result -> {
                WebResponse<ProjectResponse> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });

            assertEquals(false, response.getStatus());
        });    
    }

    @Test
    void testUpdateProjectInvalidToken() throws Exception {
        UserEntity user = userRepository.findByUsername(username).orElse(null);

        ProjectEntity project = new ProjectEntity();
        project.setName(name);
        project.setImageUrl(imageUrl);
        project.setUrl(projectUrl);
        project.setDescription(description);
        project.setHashtag(hashtag);
        project.setIsPublished(isPublish);
        project.setIsOpen(isOpen);
        project.setUserEntity(user);
        projectRepository.save(project);

        UpdateProjectRequest request = new UpdateProjectRequest();
        request.setName(name + " updated");
        request.setImageUrl(imageUrl);
        request.setUrl(projectUrl);
        request.setDescription(description + " updated");
        request.setHashtag(hashtag);
        request.setIsPublished(false);
        request.setIsOpen(false);

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
                patch("/api/users/projects/" + project.getId())
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .header("Authorization", mockBearerToken)                        
        ).andExpectAll(
                status().isUnauthorized()
        ).andDo(result -> {
                WebResponse<ProjectResponse> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });

            assertEquals(false, response.getStatus());
        });        
    }

    @Test
    void testUpdateProjectTokenExpired() throws Exception {
        UserEntity user = userRepository.findByUsername(username).orElse(null);

        ProjectEntity project = new ProjectEntity();
        project.setName(name);
        project.setImageUrl(imageUrl);
        project.setUrl(projectUrl);
        project.setDescription(description);
        project.setHashtag(hashtag);
        project.setIsPublished(isPublish);
        project.setIsOpen(isOpen);
        project.setUserEntity(user);
        projectRepository.save(project);

        UpdateProjectRequest request = new UpdateProjectRequest();
        request.setName(name + " updated");
        request.setImageUrl(imageUrl);
        request.setUrl(projectUrl);
        request.setDescription(description + " updated");
        request.setHashtag(hashtag);
        request.setIsPublished(false);
        request.setIsOpen(false);

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
                patch("/api/users/projects/" + project.getId())
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .header("Authorization", mockBearerToken)                        
        ).andExpectAll(
                status().isForbidden()
        ).andDo(result -> {
                WebResponse<ProjectResponse> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });

            assertEquals(false, response.getStatus());
        });        
    }

    @Test
    void testUpdateProjectNoToken() throws Exception {
        UserEntity user = userRepository.findByUsername(username).orElse(null);

        ProjectEntity project = new ProjectEntity();
        project.setName(name);
        project.setImageUrl(imageUrl);
        project.setUrl(projectUrl);
        project.setDescription(description);
        project.setHashtag(hashtag);
        project.setIsPublished(isPublish);
        project.setIsOpen(isOpen);
        project.setUserEntity(user);
        projectRepository.save(project);

        UpdateProjectRequest request = new UpdateProjectRequest();
        request.setName(name + " updated");
        request.setImageUrl(imageUrl);
        request.setUrl(projectUrl);
        request.setDescription(description + " updated");
        request.setHashtag(hashtag);
        request.setIsPublished(false);
        request.setIsOpen(false);

        Authentication authentication = authenticationManager.authenticate(
                                            new UsernamePasswordAuthenticationToken(
                                                username, password)
                                            );

        String mockToken = jwtUtil.generateToken(authentication);

        user.setToken(mockToken);
        user.setTokenExpiredAt(System.currentTimeMillis() + SecurityConstants.JWTexpiration);
        userRepository.save(user);        

        mockMvc.perform(
                patch("/api/users/projects/" + project.getId())
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))                                               
        ).andExpectAll(
                status().isForbidden()
        ).andDo(result -> {
                WebResponse<ProjectResponse> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });

            assertEquals(false, response.getStatus());
        });        
    }

    @Test
    void testDeleteProjectSuccess() throws Exception {
        UserEntity user = userRepository.findByUsername(username).orElse(null);

        ProjectEntity project = new ProjectEntity();
        project.setName(name);
        project.setImageUrl(imageUrl);
        project.setUrl(projectUrl);
        project.setDescription(description);
        project.setHashtag(hashtag);
        project.setIsPublished(isPublish);
        project.setIsOpen(isOpen);
        project.setUserEntity(user);
        projectRepository.save(project);

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
                delete("/api/users/projects/" + project.getId())
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)                        
                        .header("Authorization", mockBearerToken)                        
        ).andExpectAll(
                status().isOk()
        ).andDo(result -> {
                WebResponse<ProjectResponse> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });

            assertEquals(true, response.getStatus());
        });
    }

    @Test
    void testDeleteProjectNotFound() throws Exception {
        UserEntity user = userRepository.findByUsername(username).orElse(null);

        ProjectEntity project = new ProjectEntity();
        project.setName(name);
        project.setImageUrl(imageUrl);
        project.setUrl(projectUrl);
        project.setDescription(description);
        project.setHashtag(hashtag);
        project.setIsPublished(isPublish);
        project.setIsOpen(isOpen);
        project.setUserEntity(user);
        projectRepository.save(project);

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
                delete("/api/users/projects/1234")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)                        
                        .header("Authorization", mockBearerToken)                        
        ).andExpectAll(
                status().isNotFound()
        ).andDo(result -> {
                WebResponse<ProjectResponse> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });

            assertEquals(false, response.getStatus());
        });
    }

    @Test
    void testDeleteProjectBadId() throws Exception {
        UserEntity user = userRepository.findByUsername(username).orElse(null);

        ProjectEntity project = new ProjectEntity();
        project.setName(name);
        project.setImageUrl(imageUrl);
        project.setUrl(projectUrl);
        project.setDescription(description);
        project.setHashtag(hashtag);
        project.setIsPublished(isPublish);
        project.setIsOpen(isOpen);
        project.setUserEntity(user);
        projectRepository.save(project);

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
                delete("/api/users/projects/" + project.getId() + "a")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)                        
                        .header("Authorization", mockBearerToken)                        
        ).andExpectAll(
                status().isBadRequest()
        ).andDo(result -> {
                WebResponse<ProjectResponse> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });

            assertEquals(false, response.getStatus());
        });
    }

    @Test
    void testDeleteProjectInvalidToken() throws Exception {
        UserEntity user = userRepository.findByUsername(username).orElse(null);

        ProjectEntity project = new ProjectEntity();
        project.setName(name);
        project.setImageUrl(imageUrl);
        project.setUrl(projectUrl);
        project.setDescription(description);
        project.setHashtag(hashtag);
        project.setIsPublished(isPublish);
        project.setIsOpen(isOpen);
        project.setUserEntity(user);
        projectRepository.save(project);

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
                delete("/api/users/projects/" + project.getId())
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)                        
                        .header("Authorization", mockBearerToken)                        
        ).andExpectAll(
                status().isUnauthorized()
        ).andDo(result -> {
                WebResponse<ProjectResponse> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });

            assertEquals(false, response.getStatus());
        });
    }

    @Test
    void testDeleteProjectTokenExpired() throws Exception {
        UserEntity user = userRepository.findByUsername(username).orElse(null);

        ProjectEntity project = new ProjectEntity();
        project.setName(name);
        project.setImageUrl(imageUrl);
        project.setUrl(projectUrl);
        project.setDescription(description);
        project.setHashtag(hashtag);
        project.setIsPublished(isPublish);
        project.setIsOpen(isOpen);
        project.setUserEntity(user);
        projectRepository.save(project);

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
                delete("/api/users/projects/" + project.getId())
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)                        
                        .header("Authorization", mockBearerToken)                        
        ).andExpectAll(
                status().isForbidden()
        ).andDo(result -> {
                WebResponse<ProjectResponse> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });

            assertEquals(false, response.getStatus());
        });
    }

    @Test
    void testDeleteProjectNoToken() throws Exception {
        UserEntity user = userRepository.findByUsername(username).orElse(null);

        ProjectEntity project = new ProjectEntity();
        project.setName(name);
        project.setImageUrl(imageUrl);
        project.setUrl(projectUrl);
        project.setDescription(description);
        project.setHashtag(hashtag);
        project.setIsPublished(isPublish);
        project.setIsOpen(isOpen);
        project.setUserEntity(user);
        projectRepository.save(project);

        Authentication authentication = authenticationManager.authenticate(
                                            new UsernamePasswordAuthenticationToken(
                                                username, password)
                                            );

        String mockToken = jwtUtil.generateToken(authentication);

        user.setToken(mockToken);
        user.setTokenExpiredAt(System.currentTimeMillis() + SecurityConstants.JWTexpiration);
        userRepository.save(user);

        mockMvc.perform(
                delete("/api/users/projects/" + project.getId())
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)                                                                    
        ).andExpectAll(
                status().isForbidden()
        ).andDo(result -> {
                WebResponse<ProjectResponse> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });

            assertEquals(false, response.getStatus());
        });
    }
}
