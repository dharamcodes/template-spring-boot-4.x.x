package com.tragefox.api.security.model.entity;

import com.tragefox.api.common.entity.BaseEntity;
import com.tragefox.api.security.constant.RoleType;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.util.Set;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Builder
@EqualsAndHashCode(callSuper = true)
@Document(collection = "authDetailData")
public class AuthenticationEntity extends BaseEntity {

    @Id
    private String id;

    @Email(message = "Invalid email format")
    @NotBlank(message = "Email cannot be blank")
    private String email;

    @Indexed(unique = true)
    @NotBlank(message = "Identifier cannot be blank")
    @Size(min = 5, max = 15, message = "Identifier must be between 6 and 20 characters")
    private String userName;

    @NotBlank(message = "Password cannot be blank")
    @Size(min = 6, max = 20, message = "Password must be between 6 and 20 characters")
    private String password;

    private RoleType role;
    private Boolean isActive;
    private Set<String> identifiers;
}
