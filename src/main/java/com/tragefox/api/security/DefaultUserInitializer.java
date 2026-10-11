package com.tragefox.api.security;

import com.tragefox.api.application.service.AuthenticationService;
import com.tragefox.api.exception.ApplicationException;
import com.tragefox.api.security.constant.RoleType;
import com.tragefox.api.security.model.request.AuthenticationRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class DefaultUserInitializer implements ApplicationRunner {

    private final AuthenticationService authenticationService;

    @Override
    public void run(ApplicationArguments args) {
        try {
            var userDetails = prepareUserDetails();
            var userCreated = authenticationService.signup(userDetails);
            log.info("Default Super User created, please reset the password ::{}", userCreated.getUserName());
        } catch (ApplicationException ex) {
            log.error("Error occurred while creating user:: {}", ex.getMessage());
        }
    }

    private AuthenticationRequest prepareUserDetails() {
        return AuthenticationRequest.builder()
                .userName("admin")
                .password("admin")
                .email("admin@equifix.com")
                .role(RoleType.SUPER_USER)
                .isActive(true)
                .build();
    }
}
