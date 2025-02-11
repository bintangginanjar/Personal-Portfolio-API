package com.api.rest.portfolio.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.api.rest.portfolio.model.ProfileResponse;
import com.api.rest.portfolio.model.RegisterProfileRequest;
import com.api.rest.portfolio.model.UpdateProfileRequest;
import com.api.rest.portfolio.model.WebResponse;
import com.api.rest.portfolio.service.ProfileService;

@RestController
public class ProfileController {

    @Autowired
    private ProfileService profileService;

    public ProfileController(ProfileService profileService) {
        this.profileService = profileService;
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping(
        path = "/api/users/profiles",
        consumes = MediaType.APPLICATION_JSON_VALUE,   
        produces = MediaType.APPLICATION_JSON_VALUE
    )
    public WebResponse<ProfileResponse> create(Authentication authentication, 
                                            @RequestBody RegisterProfileRequest request) {
        ProfileResponse response = profileService.create(authentication, request);

        return WebResponse.<ProfileResponse>builder()
                                        .status(true)
                                        .messages("Profile register success")
                                        .data(response)
                                        .build();      
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping(
        path = "/api/users/profiles",        
        produces = MediaType.APPLICATION_JSON_VALUE
    )
    public WebResponse<ProfileResponse> get(Authentication authentication) {
        ProfileResponse response = profileService.get(authentication);

        return WebResponse.<ProfileResponse>builder()
                                        .status(true)
                                        .messages("Profile fetching success")
                                        .data(response)
                                        .build();      
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PatchMapping(
        path = "/api/users/profiles",
        consumes = MediaType.APPLICATION_JSON_VALUE,        
        produces = MediaType.APPLICATION_JSON_VALUE
    )
    public WebResponse<ProfileResponse> update(Authentication authentication, @RequestBody UpdateProfileRequest request) {
        ProfileResponse response = profileService.update(authentication, request);

        return WebResponse.<ProfileResponse>builder()
                                        .status(true)
                                        .messages("Profile update success")
                                        .data(response)
                                        .build();      
    }
}
