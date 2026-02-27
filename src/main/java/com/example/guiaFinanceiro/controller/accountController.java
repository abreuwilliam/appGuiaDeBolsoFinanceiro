package com.example.guiaFinanceiro.controller;

import com.example.guiaFinanceiro.dto.AccountDto;
import com.example.guiaFinanceiro.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/account")
public class accountController {
    @Autowired
    private  AccountService accountService;

    @PostMapping
    public ResponseEntity<AccountDto> postAccount(@Valid @RequestBody AccountDto accountDto){
     AccountDto createAccount = accountService.createAccount(accountDto);
     return ResponseEntity.status(HttpStatus.CREATED).body(createAccount);
    }

    @GetMapping("/balance/{userId}")
    public ResponseEntity<BigDecimal> getBalance(@Valid @PathVariable UUID userId){
       BigDecimal balance = accountService.findBalanceAccount(userId);
        return ResponseEntity.ok(balance);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<AccountDto>> findByUser(@PathVariable UUID userId){
        List<AccountDto> contas = accountService.findByUser(userId);
        return ResponseEntity.status(HttpStatus.CREATED).body(contas);
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        accountService.deleteAccount(id);
        return ResponseEntity.noContent().build();
    }
    @PatchMapping("/{id}")
    public ResponseEntity<AccountDto> update(@PathVariable UUID id, @RequestBody AccountDto data) {
        AccountDto updatedAccount = accountService.updateAccountPartially(id, data);
        return ResponseEntity.ok(updatedAccount);
    }
}
