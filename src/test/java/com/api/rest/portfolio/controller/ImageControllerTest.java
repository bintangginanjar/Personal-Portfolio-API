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

import com.api.rest.portfolio.entity.ImageEntity;
import com.api.rest.portfolio.entity.ProjectEntity;
import com.api.rest.portfolio.entity.RoleEntity;
import com.api.rest.portfolio.entity.UserEntity;
import com.api.rest.portfolio.model.ImageResponse;
import com.api.rest.portfolio.model.RegisterImageRequest;
import com.api.rest.portfolio.model.UpdateImageRequest;
import com.api.rest.portfolio.model.WebResponse;
import com.api.rest.portfolio.repository.ImageRepository;
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
public class ImageControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private ImageRepository imageRepository;

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

    private final String projectName = "Test Project";
    private final String projectImageUrl = "https://cdn-icons-png.flaticon.com/512/4191/4191039.png";
    private final String projectUrl = "https://cdn-icons-png.flaticon.com/512/4191/4191039.png";
    private final String projectDescription = "Project description";
    private final String projectHashtag = "#project, #springboot, #backend";
    private final Boolean projectIsPublish = true;
    private final Boolean projectIsOpen = true;

    private final String imageName = "Test Project Image";
    private final String imageUrl = "https://cdn-icons-png.flaticon.com/512/4191/4191039.png";

    @BeforeEach
    void setUp() {                

        imageRepository.deleteAll();
        projectRepository.deleteAll();
        userRepository.deleteAll();

        RoleEntity role = roleRepository.findByName("ROLE_ADMIN").orElse(null);

        UserEntity user = new UserEntity();
        user.setUsername(username);        
        user.setPassword(passwordEncoder.encode(password));
        user.setRoles(Collections.singletonList(role));        
        userRepository.save(user);

        ProjectEntity project = new ProjectEntity();
        project.setName(projectName);
        project.setImageUrl(projectImageUrl);
        project.setUrl(projectUrl);
        project.setDescription(projectDescription);
        project.setHashtag(projectHashtag);
        project.setIsPublished(projectIsPublish);
        project.setIsOpen(projectIsOpen);
        project.setUserEntity(user);
        projectRepository.save(project);
    }

    @Test
    void testRegisterImageSuccess() throws Exception {
        UserEntity user = userRepository.findByUsername(username).orElse(null);
        ProjectEntity project = projectRepository.findFirstByName(projectName).orElse(null);

        RegisterImageRequest request = new RegisterImageRequest();
        request.setName(imageName);
        request.setImageUrl(imageUrl);        

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
                post("/api/users/projects/" + project.getId())
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .header("Authorization", mockBearerToken)                        
        ).andExpectAll(
                status().isOk()
        ).andDo(result -> {
                WebResponse<ImageResponse> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });

            assertEquals(true, response.getStatus());
        });
    }

    @Test
    void testRegisterImageBlank() throws Exception {
        UserEntity user = userRepository.findByUsername(username).orElse(null);
        ProjectEntity project = projectRepository.findFirstByName(projectName).orElse(null);

        RegisterImageRequest request = new RegisterImageRequest();
        request.setName("");
        request.setImageUrl("");        

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
                post("/api/users/projects/" + project.getId())
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .header("Authorization", mockBearerToken)                        
        ).andExpectAll(
                status().isBadRequest()
        ).andDo(result -> {
                WebResponse<ImageResponse> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });

            assertEquals(false, response.getStatus());
        });
    }

    @Test
    void testRegisterImageProjectNotFound() throws Exception {
        UserEntity user = userRepository.findByUsername(username).orElse(null);
        //ProjectEntity project = projectRepository.findFirstByName(projectName).orElse(null);

        RegisterImageRequest request = new RegisterImageRequest();
        request.setName(imageName);
        request.setImageUrl(imageUrl);        

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
                post("/api/users/projects/1")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .header("Authorization", mockBearerToken)                        
        ).andExpectAll(
                status().isNotFound()
        ).andDo(result -> {
                WebResponse<ImageResponse> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });

            assertEquals(false, response.getStatus());
        });
    }

    @Test
    void testRegisterImageBadProjectId() throws Exception {
        UserEntity user = userRepository.findByUsername(username).orElse(null);
        ProjectEntity project = projectRepository.findFirstByName(projectName).orElse(null);

        RegisterImageRequest request = new RegisterImageRequest();
        request.setName(imageName);
        request.setImageUrl(imageUrl);        

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
                post("/api/users/projects/" + project.getId() + "a")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .header("Authorization", mockBearerToken)                        
        ).andExpectAll(
                status().isBadRequest()
        ).andDo(result -> {
                WebResponse<ImageResponse> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });

            assertEquals(false, response.getStatus());
        });
    }

    @Test
    void testRegisterImageInvalidToken() throws Exception {
        UserEntity user = userRepository.findByUsername(username).orElse(null);
        ProjectEntity project = projectRepository.findFirstByName(projectName).orElse(null);

        RegisterImageRequest request = new RegisterImageRequest();
        request.setName(imageName);
        request.setImageUrl(imageUrl);        

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
                post("/api/users/projects/" + project.getId())
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .header("Authorization", mockBearerToken)                        
        ).andExpectAll(
                status().isUnauthorized()
        ).andDo(result -> {
                WebResponse<ImageResponse> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });

            assertEquals(false, response.getStatus());
        });
    }

    @Test
    void testRegisterImageTokenExpired() throws Exception {
        UserEntity user = userRepository.findByUsername(username).orElse(null);
        ProjectEntity project = projectRepository.findFirstByName(projectName).orElse(null);

        RegisterImageRequest request = new RegisterImageRequest();
        request.setName(imageName);
        request.setImageUrl(imageUrl);        

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
                post("/api/users/projects/" + project.getId())
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .header("Authorization", mockBearerToken)                        
        ).andExpectAll(
                status().isForbidden()
        ).andDo(result -> {
                WebResponse<ImageResponse> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });

            assertEquals(false, response.getStatus());
        });
    }

    @Test
    void testRegisterImageNoToken() throws Exception {
        UserEntity user = userRepository.findByUsername(username).orElse(null);
        ProjectEntity project = projectRepository.findFirstByName(projectName).orElse(null);

        RegisterImageRequest request = new RegisterImageRequest();
        request.setName(imageName);
        request.setImageUrl(imageUrl);        

        Authentication authentication = authenticationManager.authenticate(
                                            new UsernamePasswordAuthenticationToken(
                                                username, password)
                                            );

        String mockToken = jwtUtil.generateToken(authentication);

        user.setToken(mockToken);
        user.setTokenExpiredAt(System.currentTimeMillis() + SecurityConstants.JWTexpiration);
        userRepository.save(user);        

        mockMvc.perform(
                post("/api/users/projects/" + project.getId())
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))                                                
        ).andExpectAll(
                status().isForbidden()
        ).andDo(result -> {
                WebResponse<ImageResponse> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });

            assertEquals(false, response.getStatus());
        });
    }

    @Test
    void testGetImageSuccess() throws Exception {
        UserEntity user = userRepository.findByUsername(username).orElse(null);
        ProjectEntity project = projectRepository.findFirstByName(projectName).orElse(null);

        ImageEntity image = new ImageEntity();
        image.setName(imageName);
        image.setImageUrl(imageUrl);
        image.setProjectEntity(project);
        imageRepository.save(image);

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
                get("/api/users/projects/" + project.getId() + "/image/" + image.getId())
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)                        
                        .header("Authorization", mockBearerToken)                        
        ).andExpectAll(
                status().isOk()
        ).andDo(result -> {
                WebResponse<ImageResponse> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });

            assertEquals(true, response.getStatus());
        });
    }

    @Test
    void testGetImageProjectNotFound() throws Exception {
        UserEntity user = userRepository.findByUsername(username).orElse(null);
        ProjectEntity project = projectRepository.findFirstByName(projectName).orElse(null);

        ImageEntity image = new ImageEntity();
        image.setName(imageName);
        image.setImageUrl(imageUrl);
        image.setProjectEntity(project);
        imageRepository.save(image);

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
                get("/api/users/projects/1/image/" + image.getId())
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)                        
                        .header("Authorization", mockBearerToken)                        
        ).andExpectAll(
                status().isNotFound()
        ).andDo(result -> {
                WebResponse<ImageResponse> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });

            assertEquals(false, response.getStatus());
        });
    }
    
    @Test
    void testGetImageBadProjectId() throws Exception {
        UserEntity user = userRepository.findByUsername(username).orElse(null);
        ProjectEntity project = projectRepository.findFirstByName(projectName).orElse(null);

        ImageEntity image = new ImageEntity();
        image.setName(imageName);
        image.setImageUrl(imageUrl);
        image.setProjectEntity(project);
        imageRepository.save(image);

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
            get("/api/users/projects/" + project.getId() + "a" + "/image/" + image.getId())
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)                        
                        .header("Authorization", mockBearerToken)                        
        ).andExpectAll(
                status().isBadRequest()
        ).andDo(result -> {
                WebResponse<ImageResponse> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });

            assertEquals(false, response.getStatus());
        });
    }

    @Test
    void testGetImageNotFound() throws Exception {
        UserEntity user = userRepository.findByUsername(username).orElse(null);
        ProjectEntity project = projectRepository.findFirstByName(projectName).orElse(null);

        ImageEntity image = new ImageEntity();
        image.setName(imageName);
        image.setImageUrl(imageUrl);
        image.setProjectEntity(project);
        imageRepository.save(image);

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
                get("/api/users/projects/" + project.getId() + "/image/0")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)                        
                        .header("Authorization", mockBearerToken)                        
        ).andExpectAll(
                status().isNotFound()
        ).andDo(result -> {
                WebResponse<ImageResponse> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });

            assertEquals(false, response.getStatus());
        });
    }

    @Test
    void testGetImageBadImageId() throws Exception {
        UserEntity user = userRepository.findByUsername(username).orElse(null);
        ProjectEntity project = projectRepository.findFirstByName(projectName).orElse(null);

        ImageEntity image = new ImageEntity();
        image.setName(imageName);
        image.setImageUrl(imageUrl);
        image.setProjectEntity(project);
        imageRepository.save(image);

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
                get("/api/users/projects/" + project.getId() + "/image/" + image.getId() + "a")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)                        
                        .header("Authorization", mockBearerToken)                        
        ).andExpectAll(
                status().isBadRequest()
        ).andDo(result -> {
                WebResponse<ImageResponse> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });

            assertEquals(false, response.getStatus());
        });
    }

    @Test
    void testGetImageInvalidToken() throws Exception {
        UserEntity user = userRepository.findByUsername(username).orElse(null);
        ProjectEntity project = projectRepository.findFirstByName(projectName).orElse(null);

        ImageEntity image = new ImageEntity();
        image.setName(imageName);
        image.setImageUrl(imageUrl);
        image.setProjectEntity(project);
        imageRepository.save(image);

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
                get("/api/users/projects/" + project.getId() + "/image/" + image.getId())
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)                        
                        .header("Authorization", mockBearerToken)                        
        ).andExpectAll(
                status().isUnauthorized()
        ).andDo(result -> {
                WebResponse<ImageResponse> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });

            assertEquals(false, response.getStatus());
        });
    }

    @Test
    void testGetImageTokenExpired() throws Exception {
        UserEntity user = userRepository.findByUsername(username).orElse(null);
        ProjectEntity project = projectRepository.findFirstByName(projectName).orElse(null);

        ImageEntity image = new ImageEntity();
        image.setName(imageName);
        image.setImageUrl(imageUrl);
        image.setProjectEntity(project);
        imageRepository.save(image);

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
                get("/api/users/projects/" + project.getId() + "/image/" + image.getId())
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)                        
                        .header("Authorization", mockBearerToken)                        
        ).andExpectAll(
                status().isForbidden()
        ).andDo(result -> {
                WebResponse<ImageResponse> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });

            assertEquals(false, response.getStatus());
        });
    }

    @Test
    void testGetImageNoToken() throws Exception {
        UserEntity user = userRepository.findByUsername(username).orElse(null);
        ProjectEntity project = projectRepository.findFirstByName(projectName).orElse(null);

        ImageEntity image = new ImageEntity();
        image.setName(imageName);
        image.setImageUrl(imageUrl);
        image.setProjectEntity(project);
        imageRepository.save(image);

        Authentication authentication = authenticationManager.authenticate(
                                            new UsernamePasswordAuthenticationToken(
                                                username, password)
                                            );

        String mockToken = jwtUtil.generateToken(authentication);

        user.setToken(mockToken);
        user.setTokenExpiredAt(System.currentTimeMillis() + SecurityConstants.JWTexpiration);
        userRepository.save(user);        

        mockMvc.perform(
                get("/api/users/projects/" + project.getId() + "/image/" + image.getId())
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)                                           
        ).andExpectAll(
                status().isForbidden()
        ).andDo(result -> {
                WebResponse<ImageResponse> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });

            assertEquals(false, response.getStatus());
        });
    }

    @Test
    void testUpdateImageSuccess() throws Exception {
        UserEntity user = userRepository.findByUsername(username).orElse(null);
        ProjectEntity project = projectRepository.findFirstByName(projectName).orElse(null);

        ImageEntity image = new ImageEntity();
        image.setName(imageName);
        image.setImageUrl(imageUrl);
        image.setProjectEntity(project);
        imageRepository.save(image);

        UpdateImageRequest request = new UpdateImageRequest();
        request.setName(imageName + " updated");
        request.setImageUrl(imageUrl);        

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
                patch("/api/users/projects/" + project.getId() + "/image/" + image.getId())
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .header("Authorization", mockBearerToken)                        
        ).andExpectAll(
                status().isOk()
        ).andDo(result -> {
                WebResponse<ImageResponse> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });

            assertEquals(true, response.getStatus());
        });
    }

    @Test
    void testUpdateImageProjectNotFound() throws Exception {
        UserEntity user = userRepository.findByUsername(username).orElse(null);
        ProjectEntity project = projectRepository.findFirstByName(projectName).orElse(null);

        ImageEntity image = new ImageEntity();
        image.setName(imageName);
        image.setImageUrl(imageUrl);
        image.setProjectEntity(project);
        imageRepository.save(image);

        UpdateImageRequest request = new UpdateImageRequest();
        request.setName(imageName + " updated");
        request.setImageUrl(imageUrl);        

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
                patch("/api/users/projects/0/image/" + image.getId())
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .header("Authorization", mockBearerToken)                        
        ).andExpectAll(
                status().isNotFound()
        ).andDo(result -> {
                WebResponse<ImageResponse> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });

            assertEquals(false, response.getStatus());
        });
    }

    @Test
    void testUpdateImageBadProjectId() throws Exception {
        UserEntity user = userRepository.findByUsername(username).orElse(null);
        ProjectEntity project = projectRepository.findFirstByName(projectName).orElse(null);

        ImageEntity image = new ImageEntity();
        image.setName(imageName);
        image.setImageUrl(imageUrl);
        image.setProjectEntity(project);
        imageRepository.save(image);

        UpdateImageRequest request = new UpdateImageRequest();
        request.setName(imageName + " updated");
        request.setImageUrl(imageUrl);        

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
                patch("/api/users/projects/" + project.getId() + "a/image/" + image.getId())
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .header("Authorization", mockBearerToken)                        
        ).andExpectAll(
                status().isBadRequest()
        ).andDo(result -> {
                WebResponse<ImageResponse> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });

            assertEquals(false, response.getStatus());
        });
    }

    @Test
    void testUpdateImageNotFound() throws Exception {
        UserEntity user = userRepository.findByUsername(username).orElse(null);
        ProjectEntity project = projectRepository.findFirstByName(projectName).orElse(null);

        ImageEntity image = new ImageEntity();
        image.setName(imageName);
        image.setImageUrl(imageUrl);
        image.setProjectEntity(project);
        imageRepository.save(image);

        UpdateImageRequest request = new UpdateImageRequest();
        request.setName(imageName + " updated");
        request.setImageUrl(imageUrl);        

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
                patch("/api/users/projects/" + project.getId() + "/image/0")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .header("Authorization", mockBearerToken)                        
        ).andExpectAll(
                status().isNotFound()
        ).andDo(result -> {
                WebResponse<ImageResponse> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });

            assertEquals(false, response.getStatus());
        });
    }

    @Test
    void testUpdateImageBadId() throws Exception {
        UserEntity user = userRepository.findByUsername(username).orElse(null);
        ProjectEntity project = projectRepository.findFirstByName(projectName).orElse(null);

        ImageEntity image = new ImageEntity();
        image.setName(imageName);
        image.setImageUrl(imageUrl);
        image.setProjectEntity(project);
        imageRepository.save(image);

        UpdateImageRequest request = new UpdateImageRequest();
        request.setName(imageName + " updated");
        request.setImageUrl(imageUrl);        

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
                patch("/api/users/projects/" + project.getId() + "/image/" + image.getId() + "a")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .header("Authorization", mockBearerToken)                        
        ).andExpectAll(
                status().isBadRequest()
        ).andDo(result -> {
                WebResponse<ImageResponse> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });

            assertEquals(false, response.getStatus());
        });
    }

    @Test
    void testUpdateImageInvalidToken() throws Exception {
        UserEntity user = userRepository.findByUsername(username).orElse(null);
        ProjectEntity project = projectRepository.findFirstByName(projectName).orElse(null);

        ImageEntity image = new ImageEntity();
        image.setName(imageName);
        image.setImageUrl(imageUrl);
        image.setProjectEntity(project);
        imageRepository.save(image);

        UpdateImageRequest request = new UpdateImageRequest();
        request.setName(imageName + " updated");
        request.setImageUrl(imageUrl);        

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
                patch("/api/users/projects/" + project.getId() + "/image/" + image.getId())
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .header("Authorization", mockBearerToken)                        
        ).andExpectAll(
                status().isUnauthorized()
        ).andDo(result -> {
                WebResponse<ImageResponse> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });

            assertEquals(false, response.getStatus());
        });
    }

    @Test
    void testUpdateImageTokenExpired() throws Exception {
        UserEntity user = userRepository.findByUsername(username).orElse(null);
        ProjectEntity project = projectRepository.findFirstByName(projectName).orElse(null);

        ImageEntity image = new ImageEntity();
        image.setName(imageName);
        image.setImageUrl(imageUrl);
        image.setProjectEntity(project);
        imageRepository.save(image);

        UpdateImageRequest request = new UpdateImageRequest();
        request.setName(imageName + " updated");
        request.setImageUrl(imageUrl);        

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
                patch("/api/users/projects/" + project.getId() + "/image/" + image.getId())
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .header("Authorization", mockBearerToken)                        
        ).andExpectAll(
                status().isForbidden()
        ).andDo(result -> {
                WebResponse<ImageResponse> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });

            assertEquals(false, response.getStatus());
        });
    }

    @Test
    void testUpdateImageNoToken() throws Exception {
        UserEntity user = userRepository.findByUsername(username).orElse(null);
        ProjectEntity project = projectRepository.findFirstByName(projectName).orElse(null);

        ImageEntity image = new ImageEntity();
        image.setName(imageName);
        image.setImageUrl(imageUrl);
        image.setProjectEntity(project);
        imageRepository.save(image);

        UpdateImageRequest request = new UpdateImageRequest();
        request.setName(imageName + " updated");
        request.setImageUrl(imageUrl);        

        Authentication authentication = authenticationManager.authenticate(
                                            new UsernamePasswordAuthenticationToken(
                                                username, password)
                                            );

        String mockToken = jwtUtil.generateToken(authentication);

        user.setToken(mockToken);
        user.setTokenExpiredAt(System.currentTimeMillis() + SecurityConstants.JWTexpiration);
        userRepository.save(user);

        mockMvc.perform(
                patch("/api/users/projects/" + project.getId() + "/image/" + image.getId())
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))                                                
        ).andExpectAll(
                status().isForbidden()
        ).andDo(result -> {
                WebResponse<ImageResponse> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });

            assertEquals(false, response.getStatus());
        });
    }

    @Test
    void testGetListImageSuccess() throws Exception {
        UserEntity user = userRepository.findByUsername(username).orElse(null);
        ProjectEntity project = projectRepository.findFirstByName(projectName).orElse(null);

        ImageEntity image = new ImageEntity();
        image.setName(imageName);
        image.setImageUrl(imageUrl);
        image.setProjectEntity(project);
        imageRepository.save(image);

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
                get("/api/users/projects/images/list")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)                        
                        .header("Authorization", mockBearerToken)                        
        ).andExpectAll(
                status().isOk()
        ).andDo(result -> {
                WebResponse<List<ImageResponse>> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });

            assertEquals(true, response.getStatus());
        });
    }

    @Test
    void testGetListImageInvalidToken() throws Exception {
        UserEntity user = userRepository.findByUsername(username).orElse(null);
        ProjectEntity project = projectRepository.findFirstByName(projectName).orElse(null);

        ImageEntity image = new ImageEntity();
        image.setName(imageName);
        image.setImageUrl(imageUrl);
        image.setProjectEntity(project);
        imageRepository.save(image);

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
                get("/api/users/projects/images/list")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)                        
                        .header("Authorization", mockBearerToken)                        
        ).andExpectAll(
                status().isUnauthorized()
        ).andDo(result -> {
                WebResponse<List<ImageResponse>> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });

            assertEquals(false, response.getStatus());
        });
    }

    @Test
    void testGetListImageTokenExpired() throws Exception {
        UserEntity user = userRepository.findByUsername(username).orElse(null);
        ProjectEntity project = projectRepository.findFirstByName(projectName).orElse(null);

        ImageEntity image = new ImageEntity();
        image.setName(imageName);
        image.setImageUrl(imageUrl);
        image.setProjectEntity(project);
        imageRepository.save(image);

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
                get("/api/users/projects/images/list")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)                        
                        .header("Authorization", mockBearerToken)                        
        ).andExpectAll(
                status().isForbidden()
        ).andDo(result -> {
                WebResponse<List<ImageResponse>> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });

            assertEquals(false, response.getStatus());
        });
    }

    @Test
    void testGetListImageNoToken() throws Exception {
        UserEntity user = userRepository.findByUsername(username).orElse(null);
        ProjectEntity project = projectRepository.findFirstByName(projectName).orElse(null);

        ImageEntity image = new ImageEntity();
        image.setName(imageName);
        image.setImageUrl(imageUrl);
        image.setProjectEntity(project);
        imageRepository.save(image);

        Authentication authentication = authenticationManager.authenticate(
                                            new UsernamePasswordAuthenticationToken(
                                                username, password)
                                            );

        String mockToken = jwtUtil.generateToken(authentication);

        user.setToken(mockToken);
        user.setTokenExpiredAt(System.currentTimeMillis() + SecurityConstants.JWTexpiration);
        userRepository.save(user);        

        mockMvc.perform(
                get("/api/users/projects/images/list")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)                                                                      
        ).andExpectAll(
                status().isForbidden()
        ).andDo(result -> {
                WebResponse<List<ImageResponse>> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });

            assertEquals(false, response.getStatus());
        });
    }

    @Test
    void testDeleteImageSuccess() throws Exception {
        UserEntity user = userRepository.findByUsername(username).orElse(null);
        ProjectEntity project = projectRepository.findFirstByName(projectName).orElse(null);

        ImageEntity image = new ImageEntity();
        image.setName(imageName);
        image.setImageUrl(imageUrl);
        image.setProjectEntity(project);
        imageRepository.save(image);

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
                delete("/api/users/projects/" + project.getId() + "/image/" + image.getId())
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
    void testDeleteImageProjectNotFound() throws Exception {
        UserEntity user = userRepository.findByUsername(username).orElse(null);
        ProjectEntity project = projectRepository.findFirstByName(projectName).orElse(null);

        ImageEntity image = new ImageEntity();
        image.setName(imageName);
        image.setImageUrl(imageUrl);
        image.setProjectEntity(project);
        imageRepository.save(image);

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
                delete("/api/users/projects/1/image/" + image.getId())
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
    void testDeleteImageBadProjectId() throws Exception {
        UserEntity user = userRepository.findByUsername(username).orElse(null);
        ProjectEntity project = projectRepository.findFirstByName(projectName).orElse(null);

        ImageEntity image = new ImageEntity();
        image.setName(imageName);
        image.setImageUrl(imageUrl);
        image.setProjectEntity(project);
        imageRepository.save(image);

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
            delete("/api/users/projects/" + project.getId() + "a" + "/image/" + image.getId())
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
    void testDeleteImageNotFound() throws Exception {
        UserEntity user = userRepository.findByUsername(username).orElse(null);
        ProjectEntity project = projectRepository.findFirstByName(projectName).orElse(null);

        ImageEntity image = new ImageEntity();
        image.setName(imageName);
        image.setImageUrl(imageUrl);
        image.setProjectEntity(project);
        imageRepository.save(image);

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
                delete("/api/users/projects/" + project.getId() + "/image/0")
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
    void testDeleteImageBadImageId() throws Exception {
        UserEntity user = userRepository.findByUsername(username).orElse(null);
        ProjectEntity project = projectRepository.findFirstByName(projectName).orElse(null);

        ImageEntity image = new ImageEntity();
        image.setName(imageName);
        image.setImageUrl(imageUrl);
        image.setProjectEntity(project);
        imageRepository.save(image);

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
                delete("/api/users/projects/" + project.getId() + "/image/" + image.getId() + "a")
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
    void testDeleteImageInvalidToken() throws Exception {
        UserEntity user = userRepository.findByUsername(username).orElse(null);
        ProjectEntity project = projectRepository.findFirstByName(projectName).orElse(null);

        ImageEntity image = new ImageEntity();
        image.setName(imageName);
        image.setImageUrl(imageUrl);
        image.setProjectEntity(project);
        imageRepository.save(image);

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
                delete("/api/users/projects/" + project.getId() + "/image/" + image.getId())
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
    void testDeleteImageTokenExpired() throws Exception {
        UserEntity user = userRepository.findByUsername(username).orElse(null);
        ProjectEntity project = projectRepository.findFirstByName(projectName).orElse(null);

        ImageEntity image = new ImageEntity();
        image.setName(imageName);
        image.setImageUrl(imageUrl);
        image.setProjectEntity(project);
        imageRepository.save(image);

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
                delete("/api/users/projects/" + project.getId() + "/image/" + image.getId())
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
    void testDeleteImageNoToken() throws Exception {
        UserEntity user = userRepository.findByUsername(username).orElse(null);
        ProjectEntity project = projectRepository.findFirstByName(projectName).orElse(null);

        ImageEntity image = new ImageEntity();
        image.setName(imageName);
        image.setImageUrl(imageUrl);
        image.setProjectEntity(project);
        imageRepository.save(image);

        Authentication authentication = authenticationManager.authenticate(
                                            new UsernamePasswordAuthenticationToken(
                                                username, password)
                                            );

        String mockToken = jwtUtil.generateToken(authentication);

        user.setToken(mockToken);
        user.setTokenExpiredAt(System.currentTimeMillis() + SecurityConstants.JWTexpiration);
        userRepository.save(user);        

        mockMvc.perform(
                delete("/api/users/projects/" + project.getId() + "/image/" + image.getId())
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
}
