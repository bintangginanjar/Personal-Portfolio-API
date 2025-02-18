package com.api.rest.portfolio.model;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RegisterImageRequest {

    @NotBlank
    private String name;

    @NotBlank
    private String imageUrl;

}
