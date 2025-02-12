package com.api.rest.portfolio.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.api.rest.portfolio.entity.ProfileEntity;
import com.api.rest.portfolio.entity.UserEntity;

@Repository
public interface ProfileRepository extends JpaRepository<ProfileEntity, Integer> {
    
    Optional<ProfileEntity> findByUserEntity(UserEntity user);

}
