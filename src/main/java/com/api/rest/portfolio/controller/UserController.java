package com.api.rest.portfolio.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;

import com.api.rest.portfolio.model.RegisterUserRequest;
import com.api.rest.portfolio.model.UpdateUserRequest;
import com.api.rest.portfolio.model.UserResponse;
import com.api.rest.portfolio.model.WebResponse;
import com.api.rest.portfolio.service.UserService;

@RestController
public class UserController {

    @Autowired
    private UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping(
        path = "/api/users",
        consumes = MediaType.APPLICATION_JSON_VALUE,       
        produces = MediaType.APPLICATION_JSON_VALUE
    )
    public WebResponse<UserResponse> create(@RequestBody RegisterUserRequest request) {
        UserResponse response = userService.register(request);

        return WebResponse.<UserResponse>builder()
                                        .status(true)
                                        .messages("User registration success")
                                        .data(response)
                                        .build();      
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping(
        path = "/api/users/current",        
        produces = MediaType.APPLICATION_JSON_VALUE
    )    
    public WebResponse<UserResponse> get(Authentication authentication) {
        UserResponse response = userService.get(authentication);

        return WebResponse.<UserResponse>builder()
                                            .status(true)
                                            .messages("User fetching success")
                                            .data(response)
                                            .build();
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PatchMapping(
        path = "/api/users/current",
        consumes = MediaType.APPLICATION_JSON_VALUE,
        produces = MediaType.APPLICATION_JSON_VALUE
    )
    public WebResponse<UserResponse> update(Authentication authentication, @RequestBody UpdateUserRequest request) {
        UserResponse response = userService.update(authentication, request);

        return WebResponse.<UserResponse>builder()
                                            .status(true)
                                            .messages("User password update success")
                                            .data(response)
                                            .build();
    }
}
