package com.tragefox.api.application.service.impl;

import com.tragefox.api.application.service.UserDetailService;
import com.tragefox.api.security.constant.RoleType;
import com.tragefox.api.security.repository.AuthenticationRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.jspecify.annotations.NonNull;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@AllArgsConstructor
public class UserDetailServiceImpl implements UserDetailService {

    private final AuthenticationRepository authenticationRepository;

    @Override
    public UserDetails loadUserByUsername(@NonNull String username) throws UsernameNotFoundException {
        var userDetail = authenticationRepository.findByUserName(username);
        return User.builder()
                .username(userDetail.getUserName())
                .password(userDetail.getPassword())
                .roles(
                        Strings.isEmpty(userDetail.getRole().getValue())
                                ? RoleType.APP_USER.name()
                                : userDetail.getRole().getValue())
                .build();
    }
}
