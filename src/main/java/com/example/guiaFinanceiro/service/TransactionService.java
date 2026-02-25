package com.example.guiaFinanceiro.service;

import com.example.guiaFinanceiro.dto.TransactionDto;
import com.example.guiaFinanceiro.entites.Account;
import com.example.guiaFinanceiro.entites.CreditCard;
import com.example.guiaFinanceiro.entites.Invoice;
import com.example.guiaFinanceiro.entites.Transaction;
import com.example.guiaFinanceiro.map.TransactionMapper;
import com.example.guiaFinanceiro.repository.AccountRepository;
import com.example.guiaFinanceiro.repository.CreditCardRepository;
import com.example.guiaFinanceiro.repository.InvoiceRepository;
import com.example.guiaFinanceiro.repository.TransactionRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.UUID;

@Service
public class TransactionService {
    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private CreditCardRepository creditCardRepository;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private InvoiceRepository invoiceRepository;

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

            Invoice invoice = invoiceRepository.findByCreditCardId(creditCard.getId()).orElseThrow(() -> new RuntimeException("fatura não encontrado"));

            invoice.setTotalAmount(invoice.getTotalAmount().add(transactionDto.getAmount()));
            BigDecimal novoLimite = creditCard.getAvailableLimit().subtract(transactionDto.getAmount());

            if (novoLimite.compareTo(BigDecimal.ZERO) < 0) {
                throw new RuntimeException("Limite insuficiente! Operação não realizada. Limite atual: R$ " +
                        creditCard.getAvailableLimit() + ", Valor solicitado: R$ " + transactionDto.getAmount());
            }

// Se passou na validação, atualiza o limite
            creditCard.setAvailableLimit(novoLimite);
            creditCardRepository.save(creditCard);

            transaction.setCreditCard(creditCard);
        }

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
    public BigDecimal purchaseGastoTotal(UUID userId){
        try {
            return transactionRepository.findTotalDebitsByUserId(userId);
        } catch (Exception e) {
            throw new RuntimeException("erro ao processar valores total " + e.getMessage());
        }
    }
    public Integer GetDiasSemGastos(UUID userId){
        try {
            return transactionRepository.countDaysWithoutExpenses(userId);
        } catch (Exception e) {
            throw new RuntimeException("erro ao processar dias " + e.getMessage());
        }
    }
    public BigDecimal getGastoConta(UUID userId) {
        try {
        return transactionRepository.sumGastoContaByUserId(userId);
        } catch (Exception e) {
            throw new RuntimeException("erro ao processar gasto conta " + e.getMessage());
        }
    }

    public BigDecimal getGastoCartao(UUID userId) {
        try {
        return transactionRepository.sumGastoCartaoByUserId(userId);
        } catch (Exception e) {
            throw new RuntimeException("erro ao processar gasto cartao " + e.getMessage());
        }
    }
}//../../src/api/api"
//    /purchase/gastoTotal/{userId}
//        /purchase/diasSemGasto/{userId}
