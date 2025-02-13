package com.api.rest.portfolio.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.api.rest.portfolio.entity.SocialAccountEntity;
import com.api.rest.portfolio.entity.UserEntity;

public interface SocialAccountRepository extends JpaRepository<SocialAccountEntity, Integer> {

    Optional<SocialAccountEntity> findFirstByUserEntityAndId(UserEntity user, Integer socialId);

    List<SocialAccountEntity> findAllByUserEntity(UserEntity user);

}
