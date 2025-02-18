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

import com.api.rest.portfolio.model.ImageResponse;
import com.api.rest.portfolio.model.RegisterImageRequest;
import com.api.rest.portfolio.model.UpdateImageRequest;
import com.api.rest.portfolio.model.WebResponse;
import com.api.rest.portfolio.service.ImageService;

@RestController
public class ImageController {

    @Autowired
    ImageService imageService;

    public ImageController(ImageService imageService) {
        this.imageService = imageService;
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping(
        path = "/api/users/projects/{projectId}",        
        consumes = MediaType.APPLICATION_JSON_VALUE,
        produces = MediaType.APPLICATION_JSON_VALUE
    )
    public WebResponse<ImageResponse> register(Authentication authentication, 
                                            @RequestBody RegisterImageRequest request,
                                            @PathVariable("projectId") String projectId) {

        ImageResponse response = imageService.create(authentication, request, projectId);

        return WebResponse.<ImageResponse>builder()
                                        .status(true)
                                        .messages("Image registration success")
                                        .data(response)
                                        .build();      
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping(
        path = "/api/users/projects/{projectId}/image/{imageId}",        
        produces = MediaType.APPLICATION_JSON_VALUE
    )
    public WebResponse<ImageResponse> get(Authentication authentication,                                             
                                            @PathVariable("projectId") String projectId,
                                            @PathVariable("imageId") String imageId) {

        ImageResponse response = imageService.get(authentication, projectId, imageId);

        return WebResponse.<ImageResponse>builder()
                                        .status(true)
                                        .messages("Image fetching success")
                                        .data(response)
                                        .build();      
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping(
        path = "/api/users/projects/images/list",
        produces = MediaType.APPLICATION_JSON_VALUE
    )
    public WebResponse<List<ImageResponse>> list(Authentication authentication) {

        List<ImageResponse> response = imageService.list(authentication);

        return WebResponse.<List<ImageResponse>>builder()
                                        .status(true)
                                        .messages("Image fetching success")
                                        .data(response)
                                        .build();      
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PatchMapping(
        path = "/api/users/projects/{projectId}/image/{imageId}",
        consumes = MediaType.APPLICATION_JSON_VALUE,
        produces = MediaType.APPLICATION_JSON_VALUE
    )
    public WebResponse<ImageResponse> update(Authentication authentication, 
                                            @RequestBody UpdateImageRequest request,
                                            @PathVariable("projectId") String projectId,
                                            @PathVariable("imageId") String imageId) {

        ImageResponse response = imageService.update(authentication, request, projectId, imageId);

        return WebResponse.<ImageResponse>builder()
                                        .status(true)
                                        .messages("Image update success")
                                        .data(response)
                                        .build();      
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @DeleteMapping(
        path = "/api/users/projects/{projectId}/image/{imageId}",
        produces = MediaType.APPLICATION_JSON_VALUE
    )
    public WebResponse<String> delete(Authentication authentication,                                            
                                            @PathVariable("projectId") String projectId,
                                            @PathVariable("imageId") String imageId) {

        imageService.delete(authentication, projectId, imageId);

        return WebResponse.<String>builder()
                                        .status(true)
                                        .messages("Image fetching success")                                        
                                        .build();      
    }

}
