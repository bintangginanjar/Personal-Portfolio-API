package com.api.rest.portfolio.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SkillResponse {

    private Integer id;

    private String name;

    private String imageUrl;

    private Boolean isPublished;

}
