package com.api.rest.portfolio.service;

import java.util.List;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.api.rest.portfolio.entity.ProjectEntity;
import com.api.rest.portfolio.entity.ImageEntity;
import com.api.rest.portfolio.entity.UserEntity;
import com.api.rest.portfolio.mapper.ResponseMapper;
import com.api.rest.portfolio.model.ImageResponse;
import com.api.rest.portfolio.model.RegisterImageRequest;
import com.api.rest.portfolio.model.UpdateImageRequest;
import com.api.rest.portfolio.repository.ImageRepository;
import com.api.rest.portfolio.repository.ProjectRepository;
import com.api.rest.portfolio.repository.UserRepository;

@Service
public class ImageService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private ImageRepository imageRepository;

    @Autowired
    private ValidationService validationService;

    

    public ImageService(UserRepository userRepository, ProjectRepository projectRepository,
            ImageRepository imageRepository, ValidationService validationService) {
        this.userRepository = userRepository;
        this.projectRepository = projectRepository;
        this.imageRepository = imageRepository;
        this.validationService = validationService;
    }

    @Transactional
    public ImageResponse create(Authentication authentication, 
                                    RegisterImageRequest request,
                                    String strProjectId) {
        validationService.validate(request);

        Integer projectId = 0;

        try {
            projectId = Integer.parseInt(strProjectId);       
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Bad request");
        }

        UserEntity user = userRepository.findByUsername(authentication.getName())
                            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
                                        
        ProjectEntity project = projectRepository.findFirstByUserEntityAndId(user, projectId)
                                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Project not found"));

        ImageEntity image = new ImageEntity();
        image.setName(request.getName());
        image.setImageUrl(request.getImageUrl());
        image.setProjectEntity(project);

        imageRepository.save(image);

        return ResponseMapper.ToImageResponseMapper(image);
    }

    @Transactional(readOnly = true)
    public ImageResponse get(Authentication authentication,
                                    String strProjectId,
                                    String strImageId) {

        Integer projectId = 0;
        Integer imageId = 0;

        try {
            projectId = Integer.parseInt(strProjectId);       
            imageId = Integer.parseInt(strImageId);       
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Bad request");
        }

        UserEntity user = userRepository.findByUsername(authentication.getName())
                            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
                                        
        ProjectEntity project = projectRepository.findFirstByUserEntityAndId(user, projectId)
                                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Project not found"));

        ImageEntity image = imageRepository.findFirstByProjectEntityAndId(project, imageId)
                            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Image not found"));

        return ResponseMapper.ToImageResponseMapper(image);
    }

    @Transactional(readOnly = true)
    public List<ImageResponse> list(Authentication authentication) {
        List<ImageEntity> images = imageRepository.findAll();

        return ResponseMapper.ToImageResponseListMapper(images);
    }

    @Transactional
    public ImageResponse update(Authentication authentication, 
                                UpdateImageRequest request,
                                String strProjectId,
                                String strImageId) {
        
        Integer projectId = 0;
        Integer imageId = 0;

        try {
            projectId = Integer.parseInt(strProjectId);       
            imageId = Integer.parseInt(strImageId);       
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Bad request");
        }

        UserEntity user = userRepository.findByUsername(authentication.getName())
                            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
                                        
        ProjectEntity project = projectRepository.findFirstByUserEntityAndId(user, projectId)
                                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Project not found"));

        ImageEntity image = imageRepository.findFirstByProjectEntityAndId(project, imageId)
                            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Image not found"));

        if (Objects.nonNull(request.getName())) {
            image.setName(request.getName());
        }

        if (Objects.nonNull(request.getImageUrl())) {
            image.setImageUrl(request.getImageUrl());            
        }

        imageRepository.save(image);

        return ResponseMapper.ToImageResponseMapper(image);
    }

    @Transactional(readOnly = true)
    public void delete(Authentication authentication,
                                    String strProjectId,
                                    String strImageId) {

        Integer projectId = 0;
        Integer imageId = 0;

        try {
            projectId = Integer.parseInt(strProjectId);       
            imageId = Integer.parseInt(strImageId);       
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Bad request");
        }

        UserEntity user = userRepository.findByUsername(authentication.getName())
                            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
                                        
        ProjectEntity project = projectRepository.findFirstByUserEntityAndId(user, projectId)
                                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Project not found"));

        ImageEntity image = imageRepository.findFirstByProjectEntityAndId(project, imageId)
                            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Image not found"));

        try {
            imageRepository.delete(image);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Image delete failed");
        }
    }
}
