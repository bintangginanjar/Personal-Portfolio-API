package com.api.rest.portfolio.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.api.rest.portfolio.entity.UserEntity;

import java.util.Optional;

public interface UserRepository extends JpaRepository<UserEntity, Integer>{

    Optional<UserEntity> findByUsername(String username);

    Optional<UserEntity> findFirstByToken(String token);

}
