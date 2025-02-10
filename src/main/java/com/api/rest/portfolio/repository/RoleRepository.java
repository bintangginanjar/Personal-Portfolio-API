package com.api.rest.portfolio.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.api.rest.portfolio.entity.RoleEntity;

public interface RoleRepository extends JpaRepository<RoleEntity, Integer>{

    Optional<RoleEntity> findByName(String name);

}
