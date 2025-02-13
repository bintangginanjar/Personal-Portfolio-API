package com.api.rest.portfolio.mapper;

import java.util.List;
import java.util.stream.Collectors;

import com.api.rest.portfolio.entity.ProfileEntity;
import com.api.rest.portfolio.entity.RoleEntity;
import com.api.rest.portfolio.entity.ServiceEntity;
import com.api.rest.portfolio.entity.SkillEntity;
import com.api.rest.portfolio.entity.SocialAccountEntity;
import com.api.rest.portfolio.entity.UserEntity;
import com.api.rest.portfolio.model.ProfileResponse;
import com.api.rest.portfolio.model.RoleResponse;
import com.api.rest.portfolio.model.ServiceResponse;
import com.api.rest.portfolio.model.SkillResponse;
import com.api.rest.portfolio.model.SocialAccountResponse;
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
                .isPublished(skill.getIsPublished())
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

    public static ServiceResponse ToServiceResponseMapper(ServiceEntity service) {
        return ServiceResponse.builder()
                .id(service.getId())
                .name(service.getName())
                .imageUrl(service.getImageUrl())
                .description(service.getDescription())
                .isPublished(service.getIsPublished())
                .build();
    }

    public static List<ServiceResponse> ToServiceResponseListMapper(List<ServiceEntity> services) {
        return services.stream()
                            .map(
                                p -> new ServiceResponse(
                                    p.getId(),
                                    p.getName(),
                                    p.getImageUrl(),
                                    p.getDescription(),
                                    p.getIsPublished()
                                )).collect(Collectors.toList());
    }

    public static SocialAccountResponse ToSocialAccountResponseMapper(SocialAccountEntity social) {
        return SocialAccountResponse.builder()
                                    .id(social.getId())
                                    .name(social.getName())
                                    .url(social.getUrl())
                                    .imageUrl(social.getImageUrl())
                                    .build();
    }

    public static List<SocialAccountResponse> ToSocialAccountResponseListMapper(List<SocialAccountEntity> socials) {
        return socials.stream()
                        .map(
                            p -> new SocialAccountResponse(
                                p.getId(),
                                p.getName(),
                                p.getUrl(),
                                p.getImageUrl(),
                                p.getIsPublished()
                            )).collect(Collectors.toList());
    }
}
