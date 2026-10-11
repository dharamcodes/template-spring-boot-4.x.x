package com.tragefox.api.security.repository;

import com.tragefox.api.security.model.entity.AuthenticationEntity;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface AuthenticationRepository extends MongoRepository<AuthenticationEntity, String> {

    AuthenticationEntity findByUserName(String userName);
}
