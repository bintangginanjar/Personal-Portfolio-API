package com.api.rest.portfolio.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RegisterProjectRequest {

    @NotBlank
    private String name;

    @NotBlank
    private String imageUrl;

    @NotBlank
    private String url;

    @NotBlank
    private String description;

    @NotBlank
    private String hashtag;

    @NotNull
    private Boolean isPublished;

    @NotNull
    private Boolean isOpen;

}
