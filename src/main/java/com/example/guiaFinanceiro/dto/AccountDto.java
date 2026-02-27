package com.example.guiaFinanceiro.dto;

import com.example.guiaFinanceiro.entites.AccountType;
import com.example.guiaFinanceiro.entites.Users;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
public class AccountDto {
   private  UUID id;
    private String name;
    private AccountType type;
    private BigDecimal balance;
    private UUID userId;
}
