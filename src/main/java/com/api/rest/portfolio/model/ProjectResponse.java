package com.api.rest.portfolio.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProjectResponse {

    private Integer id;

    private String name;

    private String imageUrl;

    private String url;

    private String description;

    private String hashtag;

    private Boolean isPublished;

    private Boolean isOpen;

}
