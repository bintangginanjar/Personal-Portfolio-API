package com.api.rest.portfolio.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UpdateProjectRequest {

    @JsonIgnore
    @NotBlank
    private String id;

    private String name;

    private String imageUrl;

    private String url;

    private String description;

    private String hashtag;

    private Boolean isPublished;

    private Boolean isOpen;

}
