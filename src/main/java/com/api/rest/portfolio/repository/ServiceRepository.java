package com.api.rest.portfolio.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.api.rest.portfolio.entity.ServiceEntity;
import com.api.rest.portfolio.entity.UserEntity;

public interface ServiceRepository extends JpaRepository<ServiceEntity, Integer> {

    Optional<ServiceEntity> findFirstByUserEntityAndId(UserEntity user, Integer skillId);

    List<ServiceEntity> findAllByUserEntity(UserEntity user);
}
