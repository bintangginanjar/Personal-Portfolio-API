package com.api.rest.portfolio.model;

import java.util.List;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TokenResponse {

    private String token;

    private final String tokenType = "Bearer ";

    private List<String> roles;

}
