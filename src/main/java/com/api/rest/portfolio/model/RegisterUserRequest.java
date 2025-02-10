package com.api.rest.portfolio.model;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RegisterUserRequest {

    @NotBlank
    @Size(max = 128)        
    private String username;
    
    @NotBlank
    @Size(max = 128)        
    private String password;

    @NotBlank
    @Size(max = 16)
    private String role;
        
}
