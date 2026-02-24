package com.example.guiaFinanceiro.service;

import com.example.guiaFinanceiro.dto.TransactionDto;
import com.example.guiaFinanceiro.entites.Transaction;
import com.example.guiaFinanceiro.map.TransactionMapper;
import com.example.guiaFinanceiro.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TransactionService {
    @Autowired
    private TransactionRepository transactionRepository;

    public TransactionDto createTransaction (TransactionDto transactionDto){
        Transaction transaction = new Transaction();
        transaction.setAmount(transactionDto.getAmount());
        transaction.setDate(transactionDto.getDate());
        transaction.setType(transactionDto.getType());
        transaction.setDescription(transactionDto.getDescription());
        transaction.setDestinationAccount(transaction.getDestinationAccount());
        transaction.setSourceAccount(transactionDto.getSourceAccount());
        transaction.setCreditCard(transactionDto.getCreditCard());

        try{
            Transaction savadTransaction = transactionRepository.save(transaction);
            return TransactionMapper.toDto(savadTransaction);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
