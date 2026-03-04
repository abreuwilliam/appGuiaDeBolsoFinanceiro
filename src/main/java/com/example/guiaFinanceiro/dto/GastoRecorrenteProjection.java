package com.example.guiaFinanceiro.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public interface GastoRecorrenteProjection {
    UUID getId();
    BigDecimal getAmount();
    String getCategory();
    String getDescription();
    LocalDate getDate();
    String getType();
}
