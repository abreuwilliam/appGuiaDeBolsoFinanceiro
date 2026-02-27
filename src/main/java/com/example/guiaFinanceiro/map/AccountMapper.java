package com.example.guiaFinanceiro.map;

import com.example.guiaFinanceiro.dto.AccountDto;
import com.example.guiaFinanceiro.entites.Account;

public class AccountMapper {
    public static AccountDto toDto(Account account) {
        if (account == null) return null;

        AccountDto dto = new AccountDto();
        dto.setId(account.getId());
        dto.setBalance(account.getBalance());
        dto.setName(account.getName());
        dto.setType(account.getType());
        dto.setUserId(account.getUsers().getId());

        // Se o seu DTO tiver um campo ID, não esqueça de passar
        // dto.setId(account.getId());

        return dto;
    }
}
