package com.example.guiaFinanceiro.dto;

import java.math.BigDecimal;

public interface FluxoCaixaMensalProjection {

    BigDecimal getTotalGanhosMes();

    BigDecimal getTotalGastosMes();

    BigDecimal getSaldoMes();

    BigDecimal getGastosDia();
}
