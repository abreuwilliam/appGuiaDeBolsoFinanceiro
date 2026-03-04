package com.example.guiaFinanceiro.dto;

import java.math.BigDecimal;

public interface MonthlyHistory {
    Integer getAno();
    Integer getMes();
    BigDecimal getTotalGastos();
    BigDecimal getTotalGanhos();
    BigDecimal getSaldoMensal();

    // Método default para formatar o mês/ano
    default String getMonthYear() {
        return String.format("%d/%d", getMes(), getAno());
    }
}