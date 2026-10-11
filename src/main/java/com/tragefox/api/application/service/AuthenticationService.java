package com.tragefox.api.application.service;

import com.tragefox.api.security.model.request.AuthenticationRequest;
import com.tragefox.api.security.model.request.LoginRequest;
import com.tragefox.api.security.model.request.ResetPasswordRequest;
import com.tragefox.api.security.model.response.AuthenticationResponse;
import com.tragefox.api.security.model.response.LoginResponse;

public interface AuthenticationService {

    LoginResponse login(LoginRequest loginRequest);

    AuthenticationResponse signup(AuthenticationRequest authenticationRequest);

    AuthenticationResponse getByUserName(String userName);

    AuthenticationResponse updateAuthenticationDetails(String userName, AuthenticationRequest authenticationRequest);

    void changePassword(ResetPasswordRequest resetPasswordRequest);

    void deactivateAuthentication(String userName);

    void resetPassword(String userName);
}
