package com.tragefox.api.security.model.entity;

import com.tragefox.api.common.entity.BaseEntity;
import com.tragefox.api.common.identifier.annotation.Base32IdGen;
import com.tragefox.api.common.identifier.annotation.Identifier;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Builder
@Base32IdGen
@EqualsAndHashCode(callSuper = true)
@Document(collection = "accountDetail")
public class AccountDetailEntity extends BaseEntity {

    @Id
    private String id;

    @NotBlank(message = "User name cannot be blank")
    private String name;

    @Email(message = "Invalid email format")
    @NotBlank(message = "Email cannot be blank")
    private String email;

    @Identifier
    private String identifier;

    @NotNull(message = "Phone number cannot be null")
    @Min(value = 1000000000L, message = "Phone number must be at least 10 digits")
    @Max(value = 999999999999L, message = "Phone number must be at most 12 digits")
    private Long phoneNumber;

    private Boolean isActive;
}
