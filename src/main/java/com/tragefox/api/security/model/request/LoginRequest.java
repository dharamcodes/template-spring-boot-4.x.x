package com.tragefox.api.security.model.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginRequest {

    @NotBlank(message = "User name cannot be blank")
    private String userName;

    @NotBlank(message = "Password cannot be blank")
    private String password;
}
