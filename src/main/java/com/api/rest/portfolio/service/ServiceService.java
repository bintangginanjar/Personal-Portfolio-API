package com.api.rest.portfolio.service;

import java.util.List;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.api.rest.portfolio.entity.ServiceEntity;
import com.api.rest.portfolio.entity.UserEntity;
import com.api.rest.portfolio.mapper.ResponseMapper;
import com.api.rest.portfolio.model.RegisterServiceRequest;
import com.api.rest.portfolio.model.ServiceResponse;
import com.api.rest.portfolio.model.UpdateServiceRequest;
import com.api.rest.portfolio.repository.ServiceRepository;
import com.api.rest.portfolio.repository.UserRepository;

@Service
public class ServiceService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ServiceRepository serviceRepository;

    @Autowired
    private ValidationService validationService;

    public ServiceService(UserRepository userRepository, ServiceRepository serviceRepository,
            ValidationService validationService) {
        this.userRepository = userRepository;
        this.serviceRepository = serviceRepository;
        this.validationService = validationService;
    }

    @Transactional
    public ServiceResponse create(Authentication authentication, RegisterServiceRequest request) {
        validationService.validate(request);            

        UserEntity user = userRepository.findByUsername(authentication.getName())
                            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        ServiceEntity service = new ServiceEntity();
        service.setName(request.getName());
        service.setImageUrl(request.getImageUrl());
        service.setDescription(request.getDescription());
        service.setIsPublished(request.getIsPublished());
        service.setUserEntity(user);
        serviceRepository.save(service);

        return ResponseMapper.ToServiceResponseMapper(service);
    }

    @Transactional(readOnly = true)
    public ServiceResponse get(Authentication authentication, String strServiceId) {
        Integer serviceId = 0;

        try {
            serviceId = Integer.parseInt(strServiceId);       
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Bad request");
        }

        UserEntity user = userRepository.findByUsername(authentication.getName())
                            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
    
        ServiceEntity service = serviceRepository.findFirstByUserEntityAndId(user, serviceId)
                                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Service not found"));

        return ResponseMapper.ToServiceResponseMapper(service);                  
    }

    @Transactional 
    public ServiceResponse update(Authentication authentication, UpdateServiceRequest request, String strServiceId) {
        Integer serviceId = 0;

        try {
            serviceId = Integer.parseInt(strServiceId);       
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Bad request");
        }

        UserEntity user = userRepository.findByUsername(authentication.getName())
                            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        ServiceEntity service = serviceRepository.findFirstByUserEntityAndId(user, serviceId)
                                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Service not found"));

        if (Objects.nonNull(request.getName())) {
            service.setName(request.getName());
        }

        if (Objects.nonNull(request.getImageUrl())) {
            service.setImageUrl(request.getImageUrl());
        }

        if (Objects.nonNull(request.getDescription())) {
            service.setDescription(request.getDescription());
        }

        if (Objects.nonNull(request.getIsPublished())) {
            service.setIsPublished(request.getIsPublished());
        }

        serviceRepository.save(service);

        return ResponseMapper.ToServiceResponseMapper(service);
    }

    @Transactional
    public void delete(Authentication authentication, String strServiceId) {
        Integer serviceId = 0;

        try {
            serviceId = Integer.parseInt(strServiceId);       
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Bad request");
        }

        UserEntity user = userRepository.findByUsername(authentication.getName())
                            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        ServiceEntity service = serviceRepository.findFirstByUserEntityAndId(user, serviceId)
                                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Service not found"));
        
        try {
            serviceRepository.delete(service);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Delete service failed");
        } 
    }

    @Transactional(readOnly = true)
    public List<ServiceResponse> list(Authentication authentication) {
        UserEntity user = userRepository.findByUsername(authentication.getName())
                            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        List<ServiceEntity> services = serviceRepository.findAllByUserEntity(user);

        return ResponseMapper.ToServiceResponseListMapper(services);
    }
}
