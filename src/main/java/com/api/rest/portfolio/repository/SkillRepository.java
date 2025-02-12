package com.api.rest.portfolio.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.api.rest.portfolio.entity.SkillEntity;
import com.api.rest.portfolio.entity.UserEntity;

@Repository
public interface SkillRepository extends JpaRepository<SkillEntity, Integer>{

    List<SkillEntity> findAllByUserEntity(UserEntity user);

    Optional<SkillEntity> findFirstByUserEntityAndId(UserEntity user, Integer skillId);

}
