package com.tragefox.api.security.model.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AccountDetailResponse {
    private String name;
    private String email;
    private String identifier;
    private Long phoneNumber;
    private Boolean isActive;
}
