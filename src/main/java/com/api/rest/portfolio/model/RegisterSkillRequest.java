package com.api.rest.portfolio.model;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RegisterSkillRequest {

    @NotBlank
    private String name;

    @NotBlank
    private String imageUrl;

    @NotBlank
    private Boolean isPublished;

}
