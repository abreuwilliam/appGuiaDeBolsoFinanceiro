package com.example.guiaFinanceiro.dto;

import com.example.guiaFinanceiro.entites.Account;
import com.example.guiaFinanceiro.entites.CreditCard;
import com.example.guiaFinanceiro.entites.TransactionCategory;
import com.example.guiaFinanceiro.entites.TransactionType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
public class TransactionDto {
    private String description;

    private BigDecimal amount;

    private TransactionType type;

    private TransactionCategory category;

    private LocalDate date;

    private UUID sourceAccount;

    private UUID destinationAccount;

    private UUID creditCardId;
}
