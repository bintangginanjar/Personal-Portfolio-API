package com.api.rest.portfolio.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.api.rest.portfolio.entity.ProjectEntity;
import com.api.rest.portfolio.entity.UserEntity;
import java.util.List;


public interface ProjectRepository extends JpaRepository<ProjectEntity, Integer> {

    Optional<ProjectEntity> findFirstByUserEntityAndId(UserEntity user, Integer projectId);

    List<ProjectEntity> findAllByUserEntity(UserEntity userEntity);

    Optional<ProjectEntity> findFirstByName(String projectName);

}
