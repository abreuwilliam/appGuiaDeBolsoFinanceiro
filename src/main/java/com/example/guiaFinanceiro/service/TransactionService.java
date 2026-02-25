package com.example.guiaFinanceiro.service;

import com.example.guiaFinanceiro.dto.TransactionDto;
import com.example.guiaFinanceiro.entites.Account;
import com.example.guiaFinanceiro.entites.CreditCard;
import com.example.guiaFinanceiro.entites.Transaction;
import com.example.guiaFinanceiro.map.TransactionMapper;
import com.example.guiaFinanceiro.repository.AccountRepository;
import com.example.guiaFinanceiro.repository.CreditCardRepository;
import com.example.guiaFinanceiro.repository.TransactionRepository;
import jakarta.transaction.Transactional;
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

    @Transactional
    public TransactionDto createTransaction(TransactionDto transactionDto) {
        Transaction transaction = new Transaction();
        transaction.setAmount(transactionDto.getAmount());
        transaction.setDate(transactionDto.getDate());
        transaction.setType(transactionDto.getType());
        transaction.setDescription(transactionDto.getDescription());


        if (transactionDto.getCreditCardId() != null) {
            CreditCard creditCard = creditCardRepository.findById(transactionDto.getCreditCardId())
                    .orElseThrow(() -> new RuntimeException("Cartão não encontrado"));

            creditCard.setAvailableLimit(creditCard.getAvailableLimit().subtract(transactionDto.getAmount()));
            creditCardRepository.save(creditCard);

            transaction.setCreditCard(creditCard);
        }

        // 2. Lógica para Conta de Origem (Ex: Transferência ou Pagamento de Fatura)
        if (transactionDto.getSourceAccount() != null) {
            Account source = accountRepository.findById(transactionDto.getSourceAccount())
                    .orElseThrow(() -> new RuntimeException("Conta de origem não encontrada"));

            source.setBalance(source.getBalance().subtract(transactionDto.getAmount()));
            accountRepository.save(source);

            transaction.setSourceAccount(source);
        }

        if (transactionDto.getDestinationAccount() != null) {
            Account destination = accountRepository.findById(transactionDto.getDestinationAccount())
                    .orElseThrow(() -> new RuntimeException("Conta de destino não encontrada"));

            destination.setBalance(destination.getBalance().add(transactionDto.getAmount()));
            accountRepository.save(destination);

            transaction.setDestinationAccount(destination);
        }

        try {
            Transaction savedTransaction = transactionRepository.save(transaction);
            return TransactionMapper.toDto(savedTransaction);
        } catch (Exception e) {
            throw new RuntimeException("Erro ao processar transação: " + e.getMessage());
        }
    }
}
