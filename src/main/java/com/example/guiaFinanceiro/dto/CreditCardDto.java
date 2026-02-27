package com.example.guiaFinanceiro.dto;

import com.example.guiaFinanceiro.entites.Users;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
public class CreditCardDto {
    private UUID id;

    private String name;

    private BigDecimal limitAmount;

    private BigDecimal availableLimit;

    private UUID userId;
}
