package com.tragefox.api.application.controller;

import com.tragefox.api.application.service.AccountDetailService;
import com.tragefox.api.security.model.request.AccountDetailRequest;
import com.tragefox.api.security.model.response.AccountDetailResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/account")
public class AccountDetailController {

    private final AccountDetailService accountDetailService;

    @PostMapping
    public ResponseEntity<AccountDetailResponse> create(@Valid @RequestBody AccountDetailRequest accountDetailRequest) {
        return ResponseEntity.ok().body(accountDetailService.createAccount(accountDetailRequest));
    }

    @GetMapping("/{identifier}")
    public ResponseEntity<AccountDetailResponse> get(@PathVariable String identifier) {
        return ResponseEntity.ok().body(accountDetailService.getAccount(identifier));
    }

    @PutMapping("/{identifier}")
    public ResponseEntity<AccountDetailResponse> update(
            @PathVariable String identifier, @Valid @RequestBody AccountDetailRequest accountDetailRequest) {
        return ResponseEntity.ok().body(accountDetailService.updateAccountDetails(identifier, accountDetailRequest));
    }

    @DeleteMapping("/{identifier}")
    public ResponseEntity<Void> deactivate(@PathVariable String identifier) {
        accountDetailService.deactivateAccount(identifier);
        return ResponseEntity.ok().build();
    }
}
