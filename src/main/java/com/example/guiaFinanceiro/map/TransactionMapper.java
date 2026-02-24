package com.example.guiaFinanceiro.map;

import com.example.guiaFinanceiro.dto.TransactionDto;
import com.example.guiaFinanceiro.entites.Transaction;

public class TransactionMapper {
    public static TransactionDto toDto(Transaction transaction) {
        if (transaction == null) {
            return null;
        }

        TransactionDto dto = new TransactionDto();
        dto.setDescription(transaction.getDescription());
        dto.setAmount(transaction.getAmount());
        dto.setType(transaction.getType());
        dto.setDate(transaction.getDate());
        dto.setSourceAccount(transaction.getSourceAccount());
        dto.setDestinationAccount(transaction.getDestinationAccount());
        dto.setCreditCard(transaction.getCreditCard());

        return dto;
    }
}
