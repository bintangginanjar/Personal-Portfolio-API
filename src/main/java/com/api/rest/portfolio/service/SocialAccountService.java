package com.api.rest.portfolio.service;

import java.util.List;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.api.rest.portfolio.entity.SocialAccountEntity;
import com.api.rest.portfolio.entity.UserEntity;
import com.api.rest.portfolio.mapper.ResponseMapper;
import com.api.rest.portfolio.model.RegisterSocialAccountRequest;
import com.api.rest.portfolio.model.SocialAccountResponse;
import com.api.rest.portfolio.model.UpdateSocialAccountRequest;
import com.api.rest.portfolio.repository.SocialAccountRepository;
import com.api.rest.portfolio.repository.UserRepository;

@Service
public class SocialAccountService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SocialAccountRepository socialAccountRepository;

    @Autowired
    private ValidationService validationService;

    public SocialAccountService(UserRepository userRepository, SocialAccountRepository socialAccountRepository,
            ValidationService validationService) {
        this.userRepository = userRepository;
        this.socialAccountRepository = socialAccountRepository;
        this.validationService = validationService;
    }

    @Transactional
    public SocialAccountResponse create(Authentication authentication, RegisterSocialAccountRequest request) {
        validationService.validate(request);

        UserEntity user = userRepository.findByUsername(authentication.getName())
                            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        SocialAccountEntity social = new SocialAccountEntity();
        social.setName(request.getName());
        social.setImageUrl(request.getImageUrl());
        social.setUrl(request.getUrl());
        social.setIsPublished(request.getIsPublished());
        social.setUserEntity(user);

        socialAccountRepository.save(social);

        return ResponseMapper.ToSocialAccountResponseMapper(social);
    }

    @Transactional(readOnly = true)
    public SocialAccountResponse get(Authentication authentication, String strSocialId) {
        Integer socialId = 0;

        try {
            socialId = Integer.parseInt(strSocialId);       
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Bad request");
        }

        UserEntity user = userRepository.findByUsername(authentication.getName())
                            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        SocialAccountEntity social = socialAccountRepository.findFirstByUserEntityAndId(user, socialId)
                            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Social account not found"));

        return ResponseMapper.ToSocialAccountResponseMapper(social);
    }

    @Transactional(readOnly = true)
    public List<SocialAccountResponse> list(Authentication authentication) {
        UserEntity user = userRepository.findByUsername(authentication.getName())
                            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
    
        List<SocialAccountEntity> socials = socialAccountRepository.findAllByUserEntity(user);

        return ResponseMapper.ToSocialAccountResponseListMapper(socials);
    }

    @Transactional
    public SocialAccountResponse update(Authentication authentication, UpdateSocialAccountRequest request, String strSocialId) {
        Integer socialId = 0;

        try {
            socialId = Integer.parseInt(strSocialId);       
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Bad request");
        }

        UserEntity user = userRepository.findByUsername(authentication.getName())
                            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        SocialAccountEntity social = socialAccountRepository.findFirstByUserEntityAndId(user, socialId)
                            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Social account not found"));
    
        if (Objects.nonNull(request.getName())) {
            social.setName(request.getName());
        }

        if (Objects.nonNull(request.getUrl())) {
            social.setUrl(request.getUrl());
        }

        if (Objects.nonNull(request.getImageUrl())) {
            social.setImageUrl(request.getImageUrl());
        }
        
        if (Objects.nonNull(request.getIsPublished())) {
            social.setIsPublished(request.getIsPublished());
        }

        socialAccountRepository.save(social);

        return ResponseMapper.ToSocialAccountResponseMapper(social);
    }

    @Transactional
    public void delete(Authentication authentication, String strSocialId) {
        Integer socialId = 0;

        try {
            socialId = Integer.parseInt(strSocialId);       
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Bad request");
        }

        UserEntity user = userRepository.findByUsername(authentication.getName())
                            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        SocialAccountEntity social = socialAccountRepository.findFirstByUserEntityAndId(user, socialId)
                            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Social account not found"));

        try {
            socialAccountRepository.delete(social);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Delete social account failed");
        }                   
    }
}
