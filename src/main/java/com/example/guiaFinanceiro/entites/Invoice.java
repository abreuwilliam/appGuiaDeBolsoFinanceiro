package com.example.guiaFinanceiro.entites;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(
        name = "invoice",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"credit_card_id", "reference_month"})
        }
)
public class Invoice {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "reference_month", nullable = false)
    private LocalDate referenceMonth;

    private BigDecimal totalAmount;

    private boolean paid;

    @ManyToOne
    private CreditCard creditCard;
}
