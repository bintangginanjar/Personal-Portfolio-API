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

import com.api.rest.portfolio.model.RegisterSocialAccountRequest;
import com.api.rest.portfolio.model.SocialAccountResponse;
import com.api.rest.portfolio.model.UpdateSocialAccountRequest;
import com.api.rest.portfolio.model.WebResponse;
import com.api.rest.portfolio.service.SocialAccountService;

@RestController
public class SocialAccountController {

    @Autowired
    private SocialAccountService socialAccountService;

    public SocialAccountController(SocialAccountService socialAccountService) {
        this.socialAccountService = socialAccountService;
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping(
        path = "/api/users/socials",
        consumes = MediaType.APPLICATION_JSON_VALUE,
        produces = MediaType.APPLICATION_JSON_VALUE
    )
    public WebResponse<SocialAccountResponse> register(Authentication authentication, 
                                            @RequestBody RegisterSocialAccountRequest request) {

        SocialAccountResponse response = socialAccountService.create(authentication, request);

        return WebResponse.<SocialAccountResponse>builder()
                                        .status(true)
                                        .messages("Social account registration success")
                                        .data(response)
                                        .build();      
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping(
        path = "/api/users/socials/{socialId}",
        produces = MediaType.APPLICATION_JSON_VALUE
    )
    public WebResponse<SocialAccountResponse> get(Authentication authentication, @PathVariable("socialId") String socialId) {
        SocialAccountResponse response = socialAccountService.get(authentication, socialId);

        return WebResponse.<SocialAccountResponse>builder()
                                        .status(true)
                                        .messages("Social account fetching success")
                                        .data(response)
                                        .build();      
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping(
        path = "/api/users/socials/list",
        produces = MediaType.APPLICATION_JSON_VALUE
    )
    public WebResponse<List<SocialAccountResponse>> list(Authentication authentication) {
        List<SocialAccountResponse> response = socialAccountService.list(authentication);

        return WebResponse.<List<SocialAccountResponse>>builder()
                                        .status(true)
                                        .messages("Social account fetching success")
                                        .data(response)
                                        .build();      
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PatchMapping(
        path = "/api/users/socials/{socialId}",
        consumes = MediaType.APPLICATION_JSON_VALUE,
        produces = MediaType.APPLICATION_JSON_VALUE
    )
    public WebResponse<SocialAccountResponse> update(Authentication authentication, 
                                            @RequestBody UpdateSocialAccountRequest request,
                                            @PathVariable("socialId") String socialId) {

        SocialAccountResponse response = socialAccountService.update(authentication, request, socialId);

        return WebResponse.<SocialAccountResponse>builder()
                                        .status(true)
                                        .messages("Social account update success")
                                        .data(response)
                                        .build();      
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @DeleteMapping(
        path = "/api/users/socials/{socialId}",
        produces = MediaType.APPLICATION_JSON_VALUE
    )
    public WebResponse<String> delete(Authentication authentication, @PathVariable("socialId") String socialId) {
        socialAccountService.delete(authentication, socialId);

        return WebResponse.<String>builder()
                                        .status(true)
                                        .messages("Social account delete success")                                        
                                        .build();      
    }
}
