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
import com.api.rest.portfolio.entity.UserEntity;
import com.api.rest.portfolio.mapper.ResponseMapper;
import com.api.rest.portfolio.model.ProjectResponse;
import com.api.rest.portfolio.model.RegisterProjectRequest;
import com.api.rest.portfolio.model.UpdateProjectRequest;
import com.api.rest.portfolio.repository.ProjectRepository;
import com.api.rest.portfolio.repository.UserRepository;


@Service
public class ProjectService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private ValidationService validationService;

    public ProjectService(UserRepository userRepository, 
                            ProjectRepository projectRepository,
                            ValidationService validationService) {
        this.userRepository = userRepository;
        this.projectRepository = projectRepository;
        this.validationService = validationService;
    }

    @Transactional
    public ProjectResponse create(Authentication authentication, RegisterProjectRequest request)  {
        validationService.validate(request);            

        UserEntity user = userRepository.findByUsername(authentication.getName())
                            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        ProjectEntity project = new ProjectEntity();
        project.setName(request.getName());
        project.setImageUrl(request.getImageUrl());
        project.setUrl(request.getUrl());
        project.setDescription(request.getDescription());
        project.setHashtag(request.getHashtag());
        project.setIsPublished(request.getIsPublished());
        project.setIsOpen(request.getIsOpen());
        project.setUserEntity(user);
        projectRepository.save(project);

        return ResponseMapper.ToProjectResponseMapper(project);
    }

    @Transactional(readOnly = true)
    public ProjectResponse get(Authentication authentication, String strProjectId) {
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

        return ResponseMapper.ToProjectResponseMapper(project);            
    }

    @Transactional(readOnly = true)
    public List<ProjectResponse> list(Authentication authentication) {
        UserEntity user = userRepository.findByUsername(authentication.getName())
                            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        List<ProjectEntity> projects = projectRepository.findAllByUserEntity(user);

        return ResponseMapper.ToProjectResponseListMapper(projects);
    }

    @Transactional
    public ProjectResponse update(Authentication authentication, UpdateProjectRequest request, String strProjectId) {
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

        if (Objects.nonNull(request.getName())) {
            project.setName(request.getName());
        }

        if (Objects.nonNull(request.getUrl())) {
            project.setUrl(request.getUrl());
        }

        if (Objects.nonNull(request.getIsPublished())) {
            project.setIsPublished(request.getIsPublished());
        }

        if (Objects.nonNull(request.getIsOpen())) {
            project.setIsOpen(request.getIsOpen());
        }

        if (Objects.nonNull(request.getImageUrl())) {
            project.setImageUrl(request.getImageUrl());
        }

        if (Objects.nonNull(request.getHashtag())) {
            project.setHashtag(request.getHashtag());
        }

        if (Objects.nonNull(request.getDescription())) {
            project.setDescription(request.getDescription());
        }

        projectRepository.save(project);

        return ResponseMapper.ToProjectResponseMapper(project);
    }

    @Transactional
    public void delete(Authentication authentication, String strProjectId) {
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

        try {
            projectRepository.delete(project);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Project skill failed");
        }
    }
}
