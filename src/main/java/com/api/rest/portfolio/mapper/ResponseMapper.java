package com.api.rest.portfolio.mapper;

import java.util.List;
import java.util.stream.Collectors;

import com.api.rest.portfolio.entity.ProfileEntity;
import com.api.rest.portfolio.entity.RoleEntity;
import com.api.rest.portfolio.entity.SkillEntity;
import com.api.rest.portfolio.entity.UserEntity;
import com.api.rest.portfolio.model.ProfileResponse;
import com.api.rest.portfolio.model.RoleResponse;
import com.api.rest.portfolio.model.SkillResponse;
import com.api.rest.portfolio.model.UserResponse;

public class ResponseMapper {
    public static UserResponse ToUserResponseMapper(UserEntity user) {        
        List<String> roles = user.getRoles().stream().map(p -> p.getName()).toList();

        return UserResponse.builder()                
                .username(user.getUsername())
                .role(roles)
                .build();
    }

    public static List<RoleResponse> ToRoleResponseList(List<RoleEntity> roles) {
        return roles.stream()
                    .map(
                        p -> new RoleResponse(
                            p.getName()
                        )).collect(Collectors.toList());
    }

    public static ProfileResponse ToProfileResponseMapper(ProfileEntity profile) {
        return ProfileResponse.builder()
                .firstname(profile.getFirstname())
                .lastname(profile.getLastname())
                .about(profile.getAbout())
                .build();
    }

    public static SkillResponse ToSkillResponseMapper(SkillEntity skill) {
        return SkillResponse.builder()
                .id(skill.getId())
                .name(skill.getName())
                .imageUrl(skill.getImageUrl())
                //.isPublished(skill.getIsPublished())
                .build();
    }

    public static List<SkillResponse> ToSkillResponseListMapper(List<SkillEntity> skills) {
        return skills.stream()
                        .map(
                            p -> new SkillResponse(
                                p.getId(),
                                p.getName(),
                                p.getImageUrl(),
                                p.getIsPublished()
                            )).collect(Collectors.toList());
    }
}
