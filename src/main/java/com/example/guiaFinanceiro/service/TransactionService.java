package com.example.guiaFinanceiro.service;

import com.example.guiaFinanceiro.dto.TransactionDto;
import com.example.guiaFinanceiro.entites.Account;
import com.example.guiaFinanceiro.entites.CreditCard;
import com.example.guiaFinanceiro.entites.Transaction;
import com.example.guiaFinanceiro.map.TransactionMapper;
import com.example.guiaFinanceiro.repository.AccountRepository;
import com.example.guiaFinanceiro.repository.CreditCardRepository;
import com.example.guiaFinanceiro.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TransactionService {
    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private CreditCardRepository creditCardRepository;

    @Autowired
    private AccountRepository accountRepository;

    public TransactionDto createTransaction (TransactionDto transactionDto){

        CreditCard creditCard = creditCardRepository.findById(transactionDto.getCreditCardId())
                .orElseThrow(() -> new RuntimeException("Cartão de Crédito não encontrado com o ID: " + transactionDto.getCreditCardId()));

        Account sourceAccount = accountRepository.findById(transactionDto.getSourceAccount()).orElseThrow(() -> new RuntimeException("Conta nao encontrada com o id " + transactionDto.getSourceAccount()));

        Account destinationAccount = accountRepository.findById(transactionDto.getDestinationAccount()).orElseThrow(() -> new RuntimeException("Conta nao encontrada com o id " + transactionDto.getDestinationAccount()));

        Transaction transaction = new Transaction();
        transaction.setAmount(transactionDto.getAmount());
        transaction.setDate(transactionDto.getDate());
        transaction.setType(transactionDto.getType());
        transaction.setDescription(transactionDto.getDescription());
        transaction.setDestinationAccount(destinationAccount);
        transaction.setSourceAccount(sourceAccount);
        transaction.setCreditCard(creditCard);

        try{
            Transaction savadTransaction = transactionRepository.save(transaction);
            return TransactionMapper.toDto(savadTransaction);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
