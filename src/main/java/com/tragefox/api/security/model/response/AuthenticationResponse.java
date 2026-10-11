package com.tragefox.api.security.model.response;

import com.tragefox.api.security.constant.RoleType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthenticationResponse {
    private String userName;
    private RoleType role;
    private Boolean isActive;
}
