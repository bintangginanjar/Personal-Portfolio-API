package com.api.rest.portfolio.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.api.rest.portfolio.entity.UserEntity;
import com.api.rest.portfolio.mapper.ResponseMapper;
import com.api.rest.portfolio.model.LoginUserRequest;
import com.api.rest.portfolio.model.TokenResponse;
import com.api.rest.portfolio.repository.UserRepository;
import com.api.rest.portfolio.security.CustomUserDetailService;
import com.api.rest.portfolio.security.JwtUtil;
import com.api.rest.portfolio.security.SecurityConstants;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private CustomUserDetailService userDetailService;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    ValidationService validationService;

    public AuthService(AuthenticationManager authenticationManager, 
                        ValidationService validationService,
                        JwtUtil jwtUtil, UserRepository userRepository) {        
        this.authenticationManager = authenticationManager;
        this.validationService = validationService;
        this.jwtUtil = jwtUtil;
        this.userRepository = userRepository;
    }

    @Transactional
    public TokenResponse login(LoginUserRequest request) {
        try {
            Authentication authentication =  authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                                    request.getUsername(), request.getPassword())
                                );
                                            
            SecurityContextHolder.getContext().setAuthentication(authentication);

            UserDetails userDetails = userDetailService.loadUserByUsername(request.getUsername());

            List<String> roles = userDetails.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.toList());
            
            UserEntity user = userRepository.findByUsername(authentication.getName())
                            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

            String token = jwtUtil.generateToken(authentication);

            user.setToken(token);
            user.setTokenExpiredAt(System.currentTimeMillis() + SecurityConstants.JWTexpiration);
            userRepository.save(user);

            //return TokenResponse.builder().token(token).roles(roles).build();
            return ResponseMapper.ToTokenResponseMapper(user, token, roles);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Wrong username or password");
        }        
    }

    @Transactional
    public void logout(Authentication authentication, HttpServletRequest request, HttpServletResponse response) {
        
        if (authentication != null && authentication.isAuthenticated()) {
            new SecurityContextLogoutHandler().logout(request, response, authentication);
        } else {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Username not found");
        }

    }

}
