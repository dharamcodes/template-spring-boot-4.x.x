package com.tragefox.api.security.repository;

import com.tragefox.api.security.model.entity.AccountDetailEntity;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface AccountDetailRepository extends MongoRepository<AccountDetailEntity, String> {
    AccountDetailEntity findByIdentifier(String identifier);

    AccountDetailEntity findByEmailAndIsActive(String email, boolean active);
}
