package com.api.rest.portfolio.service;

import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.api.rest.portfolio.entity.ProfileEntity;
import com.api.rest.portfolio.entity.UserEntity;
import com.api.rest.portfolio.mapper.ResponseMapper;
import com.api.rest.portfolio.model.ProfileResponse;
import com.api.rest.portfolio.model.RegisterProfileRequest;
import com.api.rest.portfolio.model.UpdateProfileRequest;
import com.api.rest.portfolio.repository.ProfileRepository;
import com.api.rest.portfolio.repository.UserRepository;

@Service
public class ProfileService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProfileRepository profileRepository;

    @Autowired
    private ValidationService validationService;

    public ProfileService(ProfileRepository profileRepository, ValidationService validationService) {
        this.profileRepository = profileRepository;
        this.validationService = validationService;
    }

    @Transactional
    public ProfileResponse create(Authentication authentication, RegisterProfileRequest request) {
        validationService.validate(request);            

        ProfileEntity profile = new ProfileEntity();
        profile.setFirstname(request.getFirstname());
        profile.setLastname(request.getLastname());
        profile.setAbout(request.getAbout());
        profileRepository.save(profile);

        return ResponseMapper.ToProfileResponseMapper(profile);
    }

    @Transactional(readOnly = true)
    public ProfileResponse get(Authentication authentication) {
        UserEntity user = userRepository.findByUsername(authentication.getName())
                            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));                    

        ProfileEntity profile = profileRepository.findByUserEntity(user)
                                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Profile not found"));

        return ResponseMapper.ToProfileResponseMapper(profile);
    }

    @Transactional
    public ProfileResponse update(Authentication authentication, UpdateProfileRequest request) {
        UserEntity user = userRepository.findByUsername(authentication.getName())
                            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));                    

        ProfileEntity profile = profileRepository.findByUserEntity(user)
                                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Profile not found"));

        if (Objects.nonNull(request.getFirstname())) {
            profile.setFirstname(request.getFirstname());
        }

        if (Objects.nonNull(request.getLastname())) {
            profile.setLastname(request.getLastname());
        }

        if (Objects.nonNull(request.getAbout())) {
            profile.setAbout(request.getAbout());
        }

        profileRepository.save(profile);

        return ResponseMapper.ToProfileResponseMapper(profile);
    }
}
