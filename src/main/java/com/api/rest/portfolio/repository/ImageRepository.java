package com.api.rest.portfolio.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.api.rest.portfolio.entity.ImageEntity;
import com.api.rest.portfolio.entity.ProjectEntity;
import com.api.rest.portfolio.entity.UserEntity;

public interface ImageRepository extends JpaRepository<ImageEntity, Integer>{

    List<ImageEntity> findAllByUserEntity(UserEntity user);

    Optional<ImageEntity> findFirstByProjectEntityAndId(ProjectEntity project, Integer imageId);

}
