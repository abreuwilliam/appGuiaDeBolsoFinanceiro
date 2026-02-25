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
        if (transaction.getSourceAccount() != null) {
            dto.setSourceAccount(transaction.getSourceAccount().getId());
        }

        if (transaction.getDestinationAccount() != null) {
            dto.setDestinationAccount(transaction.getDestinationAccount().getId());
        }

        if (transaction.getCreditCard() != null) {
            dto.setCreditCardId(transaction.getCreditCard().getId());
        }

        return dto;
    }
}
