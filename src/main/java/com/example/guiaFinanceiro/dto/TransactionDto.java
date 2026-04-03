package com.example.guiaFinanceiro.dto;

import com.example.guiaFinanceiro.entites.*;
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
    private Integer installments; // null ou 1 = compra à vista

    private BigDecimal amount;

    private TransactionType type;

    private TransactionCategory category;

    private LocalDate date;

    private UUID sourceAccount;

    private UUID destinationAccount;

    private UUID creditCardId;

    private UUID financialGoal;
}
