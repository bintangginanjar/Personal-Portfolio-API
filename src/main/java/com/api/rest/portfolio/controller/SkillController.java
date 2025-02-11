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

import com.api.rest.portfolio.model.RegisterSkillRequest;
import com.api.rest.portfolio.model.SkillResponse;
import com.api.rest.portfolio.model.UpdateSkillRequest;
import com.api.rest.portfolio.model.WebResponse;
import com.api.rest.portfolio.service.SkillService;

@RestController
public class SkillController {

    @Autowired
    private SkillService skillService;

    public SkillController(SkillService skillService) {
        this.skillService = skillService;
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping(
        path = "/api/users/skills",        
        consumes = MediaType.APPLICATION_JSON_VALUE,
        produces = MediaType.APPLICATION_JSON_VALUE
    )
    public WebResponse<SkillResponse> get(Authentication authentication, 
                                            @RequestBody RegisterSkillRequest request) {
        SkillResponse response = skillService.create(authentication, request);

        return WebResponse.<SkillResponse>builder()
                                        .status(true)
                                        .messages("Skill register success")
                                        .data(response)
                                        .build();      
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping(
        path = "/api/users/skills",        
        produces = MediaType.APPLICATION_JSON_VALUE
    )
    public WebResponse<List<SkillResponse>> get(Authentication authentication) {
        List<SkillResponse> response = skillService.get(authentication);

        return WebResponse.<List<SkillResponse>>builder()
                                        .status(true)
                                        .messages("Skill register success")
                                        .data(response)
                                        .build();      
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PatchMapping(
        path = "/api/users/skills/{skillId}",
        consumes = MediaType.APPLICATION_JSON_VALUE,     
        produces = MediaType.APPLICATION_JSON_VALUE
    )
    public WebResponse<SkillResponse> update(Authentication authentication,
                                                @RequestBody UpdateSkillRequest request,
                                                @PathVariable("skillId") String skillId) {
        SkillResponse response = skillService.update(authentication, request, skillId);

        return WebResponse.<SkillResponse>builder()
                                        .status(true)
                                        .messages("Skill register success")
                                        .data(response)
                                        .build();      
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @DeleteMapping(
        path = "/api/users/skills/{skillId}",        
        produces = MediaType.APPLICATION_JSON_VALUE
    )
    public WebResponse<String> update(Authentication authentication,                                                
                                        @PathVariable("skillId") String skillId) {

        skillService.delete(authentication, skillId);

        return WebResponse.<String>builder()
                                        .status(true)
                                        .messages("Skill register success")                                        
                                        .build();      
    }
}
