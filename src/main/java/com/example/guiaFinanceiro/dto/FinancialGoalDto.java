package com.example.guiaFinanceiro.dto;

import com.example.guiaFinanceiro.entites.Users;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
public class FinancialGoalDto {

    private UUID id;

    private String name;

    private BigDecimal targetAmount;

    private BigDecimal currentAmount;

    private LocalDate targetDate;

    private boolean completed;

    private UUID user;
}


