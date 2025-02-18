package com.api.rest.portfolio.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.api.rest.portfolio.entity.ImageEntity;
import com.api.rest.portfolio.entity.ProjectEntity;

public interface ImageRepository extends JpaRepository<ImageEntity, Integer>{

    Optional<ImageEntity> findFirstByProjectEntityAndId(ProjectEntity project, Integer imageId);

}
