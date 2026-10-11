package com.tragefox.api.application.service;

import com.tragefox.api.security.model.request.AccountDetailRequest;
import com.tragefox.api.security.model.response.AccountDetailResponse;

public interface AccountDetailService {
    AccountDetailResponse createAccount(AccountDetailRequest accountDetailRequest);

    AccountDetailResponse getAccount(String identifier);

    AccountDetailResponse updateAccountDetails(String identifier, AccountDetailRequest accountDetailRequest);

    void deactivateAccount(String identiFier);
}
