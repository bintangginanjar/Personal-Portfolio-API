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
public class RegisterProfileRequest {

    @NotBlank
    private String firstname;
    
    @NotBlank
    private String lastname;    

    @NotBlank
    private String about;

}
