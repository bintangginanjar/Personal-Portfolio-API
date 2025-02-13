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

import com.api.rest.portfolio.model.RegisterServiceRequest;
import com.api.rest.portfolio.model.ServiceResponse;
import com.api.rest.portfolio.model.UpdateServiceRequest;
import com.api.rest.portfolio.model.WebResponse;
import com.api.rest.portfolio.service.ServiceService;

@RestController
public class ServiceController {

    @Autowired
    ServiceService serviceService;

    public ServiceController(ServiceService serviceService) {
        this.serviceService = serviceService;
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping(
        path = "/api/users/services",        
        consumes = MediaType.APPLICATION_JSON_VALUE,
        produces = MediaType.APPLICATION_JSON_VALUE
    )
    public WebResponse<ServiceResponse> register(Authentication authentication, 
                                            @RequestBody RegisterServiceRequest request) {

        ServiceResponse response = serviceService.create(authentication, request);

        return WebResponse.<ServiceResponse>builder()
                                        .status(true)
                                        .messages("Service registration success")
                                        .data(response)
                                        .build();      
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping(
        path = "/api/users/services/{serviceId}",                
        produces = MediaType.APPLICATION_JSON_VALUE
    )
    public WebResponse<ServiceResponse> get(Authentication authentication,
                                            @PathVariable("serviceId") String serviceId) {

        ServiceResponse response = serviceService.get(authentication, serviceId);

        return WebResponse.<ServiceResponse>builder()
                                        .status(true)
                                        .messages("Service fetching success")
                                        .data(response)
                                        .build();      
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PatchMapping(
        path = "/api/users/services/{serviceId}",
        consumes = MediaType.APPLICATION_JSON_VALUE,
        produces = MediaType.APPLICATION_JSON_VALUE
    )
    public WebResponse<ServiceResponse> update(Authentication authentication, 
                                            @RequestBody UpdateServiceRequest request,
                                            @PathVariable("serviceId") String serviceId) {

        ServiceResponse response = serviceService.update(authentication, request, serviceId);

        return WebResponse.<ServiceResponse>builder()
                                        .status(true)
                                        .messages("Service update success")
                                        .data(response)
                                        .build();      
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @DeleteMapping(
        path = "/api/users/services/{serviceId}",                
        produces = MediaType.APPLICATION_JSON_VALUE
    )
    public WebResponse<String> delete(Authentication authentication,
                                            @PathVariable("serviceId") String serviceId) {

        serviceService.delete(authentication, serviceId);

        return WebResponse.<String>builder()
                                        .status(true)
                                        .messages("Service delete success")                                        
                                        .build();      
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping(
        path = "/api/users/services/list",                
        produces = MediaType.APPLICATION_JSON_VALUE
    )
    public WebResponse<List<ServiceResponse>> list(Authentication authentication) {

        List<ServiceResponse> response = serviceService.list(authentication);

        return WebResponse.<List<ServiceResponse>>builder()
                                        .status(true)
                                        .messages("Service fetching success")
                                        .data(response)
                                        .build();      
    }

}
