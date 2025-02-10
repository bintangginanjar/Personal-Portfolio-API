package com.api.rest.portfolio.service;

import java.util.Collections;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.api.rest.portfolio.entity.RoleEntity;
import com.api.rest.portfolio.entity.UserEntity;
import com.api.rest.portfolio.mapper.ResponseMapper;
import com.api.rest.portfolio.model.RegisterUserRequest;
import com.api.rest.portfolio.model.UserResponse;
import com.api.rest.portfolio.repository.RoleRepository;
import com.api.rest.portfolio.repository.UserRepository;

@Service
public class UserService {

    @Autowired
    UserRepository userRepository;

    @Autowired
    RoleRepository roleRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    private ValidationService validationService;    

    public UserService(UserRepository userRepository, RoleRepository roleRepository,
            ValidationService validationService) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.validationService = validationService;
    }

    @Transactional
    public UserResponse register(RegisterUserRequest request) {
        validationService.validate(request);

        if (userRepository.findByUsername(request.getUsername()).isPresent()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Username already registered");
        }

        RoleEntity role = roleRepository.findByName(request.getRole()).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Roles not found"));

        UserEntity user = new UserEntity();
        user.setUsername(request.getUsername());        
        user.setPassword(passwordEncoder.encode(request.getPassword()));    
        user.setRoles(Collections.singletonList(role));        

        userRepository.save(user);        

        return ResponseMapper.ToUserResponseMapper(user);
    }

}
