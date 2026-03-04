package com.example.guiaFinanceiro.dto;

import java.math.BigDecimal;

public interface TransactionCategoryView {
    String getCategory();
    BigDecimal getAmount();
}
