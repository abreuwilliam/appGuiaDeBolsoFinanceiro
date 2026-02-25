package com.example.guiaFinanceiro.dto;

import com.example.guiaFinanceiro.entites.CreditCard;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
public class InvoiceDto {

    private LocalDate referenceMonth;

    private BigDecimal totalAmount;

    private boolean paid;

    private UUID creditCardId;

}
