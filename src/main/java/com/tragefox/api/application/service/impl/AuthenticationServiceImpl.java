package com.tragefox.api.application.service.impl;

import com.tragefox.api.application.service.AuthenticationService;
import com.tragefox.api.application.service.mapper.AuthenticationServiceMapper;
import com.tragefox.api.common.mapper.MapperFactory;
import com.tragefox.api.common.mapper.MapperType;
import com.tragefox.api.exception.ApplicationException;
import com.tragefox.api.exception.constant.ErrorType;
import com.tragefox.api.security.constant.RoleType;
import com.tragefox.api.security.helper.EmailHelper;
import com.tragefox.api.security.helper.JwtTokenHelper;
import com.tragefox.api.security.model.request.AuthenticationRequest;
import com.tragefox.api.security.model.request.LoginRequest;
import com.tragefox.api.security.model.request.ResetPasswordRequest;
import com.tragefox.api.security.model.response.AuthenticationResponse;
import com.tragefox.api.security.model.response.LoginResponse;
import com.tragefox.api.security.repository.AuthenticationRepository;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthenticationServiceImpl implements AuthenticationService {

    private static final String ROLE = "role";
    private static final String PASSWORD_RESET_CLAIM = "resetPassword";
    private static final String AUTHORIZATION = "Authorization";
    private static final String RESET_PASSWORD = "reset-password";

    private final AuthenticationManager authenticationManager;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final AuthenticationRepository authenticationRepository;
    private final MapperFactory<AuthenticationServiceMapper> mapperFactory;

    @Override
    public LoginResponse login(LoginRequest loginRequest) {
        var userDetails = authenticationRepository.findByUserName(loginRequest.getUserName());
        if (ObjectUtils.isEmpty(userDetails)) {
            log.error("User with userName {} does not exist", loginRequest.getUserName());
            throw new ApplicationException(ErrorType.DATA_NOT_FOUND);
        } else if (!Boolean.TRUE.equals(userDetails.getIsActive())) {
            log.error("Inactive user with userName :: {}", loginRequest.getUserName());
            throw new ApplicationException(ErrorType.USER_NOT_ACTIVE);
        } else if (!bCryptPasswordEncoder.matches(loginRequest.getPassword(), userDetails.getPassword())) {
            log.error("Invalid userName :: {}", loginRequest.getUserName());
            throw new ApplicationException(ErrorType.ACCESS_DENIED);
        }
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getUserName(), loginRequest.getPassword()));
        var token = JwtTokenHelper.generateToken(
                loginRequest.getUserName(), Map.of(ROLE, userDetails.getRole().getValue()));
        return LoginResponse.builder()
                .userName(loginRequest.getUserName())
                .token(token)
                .build();
    }

    @Override
    public AuthenticationResponse signup(AuthenticationRequest authenticationRequest) {
        var mapper = mapperFactory.getMapper(MapperType.AUTHENTICATION_MAPPER);
        if (!ObjectUtils.isEmpty(authenticationRepository.findByUserName(authenticationRequest.getUserName()))) {
            log.error("User with given userName already exists {}", authenticationRequest.getUserName());
            throw new ApplicationException(ErrorType.DATA_EXISTS);
        }
        var encodedPassword = bCryptPasswordEncoder.encode(authenticationRequest.getPassword());
        var userEntity = mapper.requestToEntity(authenticationRequest);
        userEntity.setPassword(encodedPassword);
        if (authenticationRequest.getUserName().isBlank()) {
            authenticationRequest.setUserName(authenticationRequest.getEmail());
        }
        var userResponse = authenticationRepository.save(userEntity);
        return mapper.entityToResponse(userResponse);
    }

    @Override
    public AuthenticationResponse getByUserName(String userName) {
        var mapper = mapperFactory.getMapper(MapperType.AUTHENTICATION_MAPPER);
        var userDetails = authenticationRepository.findByUserName(userName);
        if (ObjectUtils.isEmpty(userDetails)) {
            log.error("User does not exists for userName :: {}", userName);
            throw new ApplicationException(ErrorType.DATA_NOT_FOUND);
        }
        return mapper.entityToResponse(userDetails);
    }

    @Override
    public AuthenticationResponse updateAuthenticationDetails(
            String userName, AuthenticationRequest authenticationRequest) {
        var mapper = mapperFactory.getMapper(MapperType.AUTHENTICATION_MAPPER);
        var authenticationDetails = authenticationRepository.findByUserName(userName);
        if (ObjectUtils.isEmpty(authenticationDetails)) {
            log.error("User with given userName not exists {}", authenticationRequest.getUserName());
            throw new ApplicationException(ErrorType.USER_NOT_FOUND);
        }
        mapper.merge(authenticationRequest, authenticationDetails);
        log.info("Patched authEntity for userName:: {}", authenticationDetails.getUserName());
        var authDetailsEntity = authenticationRepository.save(authenticationDetails);
        return mapper.entityToResponse(authDetailsEntity);
    }

    @Override
    public void changePassword(ResetPasswordRequest resetPasswordRequest) {
        var userName = resetPasswordRequest.getUserName();
        if (!resetPasswordRequest.getPassword().equals(resetPasswordRequest.getConfirmPassword())) {
            log.error("Password mismatch while resetting password for userName:: {} ", userName);
            throw new ApplicationException(ErrorType.PASSWORD_MISMATCH);
        }
        var password = resetPasswordRequest.getPassword();
        var mapper = mapperFactory.getMapper(MapperType.AUTHENTICATION_MAPPER);
        var authDetails = authenticationRepository.findByUserName(userName);
        if (!Boolean.TRUE.equals(authDetails.getIsActive())) {
            log.error("User is not active for userName:: {} ", userName);
            throw new ApplicationException(ErrorType.USER_NOT_ACTIVE);
        }
        var mergeRequest = AuthenticationRequest.builder()
                .password(bCryptPasswordEncoder.encode(password))
                .build();
        mapper.merge(mergeRequest, authDetails);
        authenticationRepository.save(authDetails);
    }

    @Override
    public void deactivateAuthentication(String userName) {
        var mapper = mapperFactory.getMapper(MapperType.AUTHENTICATION_MAPPER);
        var authenticationDetails = authenticationRepository.findByUserName(userName);
        if (ObjectUtils.isEmpty(authenticationDetails)) {
            log.error("User with given userName does not exists {}", userName);
            throw new ApplicationException(ErrorType.DATA_NOT_FOUND);
        }
        var mergeRequest = AuthenticationRequest.builder().isActive(false).build();
        mapper.merge(mergeRequest, authenticationDetails);
        authenticationRepository.save(authenticationDetails);
    }

    @Override
    public void resetPassword(String userName) {
        var userDetails = authenticationRepository.findByUserName(userName);
        if (ObjectUtils.isEmpty(userDetails)) {
            log.error("User with userName {} does not exists", userName);
            throw new ApplicationException(ErrorType.USER_NOT_FOUND);
        }
        Map<String, String> claims = Map.of(PASSWORD_RESET_CLAIM, RoleType.PASSWORD_RESET_CLAIM.getValue());
        var token = JwtTokenHelper.generateResetPasswordToken(userName, claims);
        EmailHelper.sendPasswordResetEmail(userDetails.getEmail(), token);
    }
}
