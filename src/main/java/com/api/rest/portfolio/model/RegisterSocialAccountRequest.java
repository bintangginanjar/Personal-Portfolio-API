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
public class RegisterSocialAccountRequest {

    @NotBlank
    private String name;

    @NotBlank
    private String url;

    @NotBlank
    private String imageUrl;

    @NotNull
    private Boolean isPublished;

}
