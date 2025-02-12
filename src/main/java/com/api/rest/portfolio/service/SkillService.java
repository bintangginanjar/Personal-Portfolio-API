package com.api.rest.portfolio.service;

import java.util.List;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.api.rest.portfolio.entity.SkillEntity;
import com.api.rest.portfolio.entity.UserEntity;
import com.api.rest.portfolio.mapper.ResponseMapper;
import com.api.rest.portfolio.model.RegisterSkillRequest;
import com.api.rest.portfolio.model.SkillResponse;
import com.api.rest.portfolio.model.UpdateSkillRequest;
import com.api.rest.portfolio.repository.SkillRepository;
import com.api.rest.portfolio.repository.UserRepository;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class SkillService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SkillRepository skillRepository;

    @Autowired
    private ValidationService validationService;

    public SkillService(UserRepository userRepository, SkillRepository skillRepository,
            ValidationService validationService) {
        this.userRepository = userRepository;
        this.skillRepository = skillRepository;
        this.validationService = validationService;
    }

    @Transactional
    public SkillResponse create(Authentication authentication, RegisterSkillRequest request) {
        validationService.validate(request);            

        UserEntity user = userRepository.findByUsername(authentication.getName())
                            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));                    

        SkillEntity skill = new SkillEntity();
        skill.setName(request.getName());
        skill.setImageUrl(request.getImageUrl());
        skill.setIsPublished(true);
        skill.setUserEntity(user);
        skillRepository.save(skill);

        return ResponseMapper.ToSkillResponseMapper(skill);
    }

    @Transactional(readOnly = true)
    public List<SkillResponse> get(Authentication authentication) {

        UserEntity user = userRepository.findByUsername(authentication.getName())
                            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        List<SkillEntity> skills = skillRepository.findAllByUserEntity(user);

        return ResponseMapper.ToSkillResponseListMapper(skills);
    }

    @Transactional
    public SkillResponse update(Authentication authentication, UpdateSkillRequest request, String strSkillId) {
        Integer skillId = 0;

        try {
            skillId = Integer.parseInt(strSkillId);       
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Bad request");
        }

        UserEntity user = userRepository.findByUsername(authentication.getName())
                            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        SkillEntity skill = skillRepository.findFirstByUserEntityAndId(user, skillId)
                            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Skill not found"));

        if (Objects.nonNull(request.getName())) {
            skill.setName(request.getName());
        }

        if (Objects.nonNull(request.getImageUrl())) {
            skill.setImageUrl(request.getImageUrl());
        }
        /*
        if (Objects.nonNull(request.getIsPublished())) {
            skill.setIsPublished(request.getIsPublished());
        }
        */
        skillRepository.save(skill);

        return ResponseMapper.ToSkillResponseMapper(skill);
    }

    @Transactional
    public void delete(Authentication authentication, String strSkillId) {
        Integer skillId = 0;

        try {
            skillId = Integer.parseInt(strSkillId);       
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Bad request");
        }

        UserEntity user = userRepository.findByUsername(authentication.getName())
                            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        SkillEntity skill = skillRepository.findFirstByUserEntityAndId(user, skillId)
                            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Skill not found"));
        try {
            skillRepository.delete(skill);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Delete skill failed");
        }        
    }

}
