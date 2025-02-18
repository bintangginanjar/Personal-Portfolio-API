package com.api.rest.portfolio.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.api.rest.portfolio.model.ProjectResponse;
import com.api.rest.portfolio.model.RegisterProjectRequest;
import com.api.rest.portfolio.model.UpdateProjectRequest;
import com.api.rest.portfolio.model.WebResponse;
import com.api.rest.portfolio.service.ProjectService;

@RestController
public class ProjectController {

    @Autowired
    ProjectService projectService;

    public ProjectController(ProjectService projectService) {
        this.projectService = projectService;
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping(
        path = "/api/users/projects",        
        consumes = MediaType.APPLICATION_JSON_VALUE,
        produces = MediaType.APPLICATION_JSON_VALUE
    )
    public WebResponse<ProjectResponse> register(Authentication authentication, 
                                            @RequestBody RegisterProjectRequest request) {

        ProjectResponse response = projectService.create(authentication, request);

        return WebResponse.<ProjectResponse>builder()
                                        .status(true)
                                        .messages("Project registration success")
                                        .data(response)
                                        .build();      
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping(
        path = "/api/users/projects/{projectId}",                
        produces = MediaType.APPLICATION_JSON_VALUE
    )
    public WebResponse<ProjectResponse> register(Authentication authentication, 
                                                @PathVariable("projectId") String projectId) {

        ProjectResponse response = projectService.get(authentication, projectId);

        return WebResponse.<ProjectResponse>builder()
                                        .status(true)
                                        .messages("Project fetching success")
                                        .data(response)
                                        .build();      
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping(
        path = "/api/users/project/list",                
        produces = MediaType.APPLICATION_JSON_VALUE
    )
    public WebResponse<List<ProjectResponse>> get(Authentication authentication) {

        List<ProjectResponse> response = projectService.list(authentication);

        return WebResponse.<List<ProjectResponse>>builder()
                                        .status(true)
                                        .messages("Project fetching success")
                                        .data(response)
                                        .build();      
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PatchMapping(
        path = "/api/users/projects/{projectId}",        
        consumes = MediaType.APPLICATION_JSON_VALUE,
        produces = MediaType.APPLICATION_JSON_VALUE
    )
    public WebResponse<ProjectResponse> update(Authentication authentication, 
                                            @RequestBody UpdateProjectRequest request,
                                            @PathVariable("projectId") String projectId) {

        request.setId(projectId);

        ProjectResponse response = projectService.update(authentication, request, projectId);

        return WebResponse.<ProjectResponse>builder()
                                        .status(true)
                                        .messages("Project update success")
                                        .data(response)
                                        .build();      
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @DeleteMapping(
        path = "/api/users/projects/{projectId}",                
        produces = MediaType.APPLICATION_JSON_VALUE
    )
    public WebResponse<String> delete(Authentication authentication, 
                                                @PathVariable("projectId") String projectId) {

        projectService.delete(authentication, projectId);

        return WebResponse.<String>builder()
                                        .status(true)
                                        .messages("Project delete success")                                        
                                        .build();      
    }

}
