package com.api.rest.portfolio.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.api.rest.portfolio.entity.ProfileEntity;
import com.api.rest.portfolio.entity.UserEntity;

public interface ProfileRepository extends JpaRepository<ProfileEntity, Integer> {
    
    Optional<ProfileEntity> findByUserEntity(UserEntity user);

}
