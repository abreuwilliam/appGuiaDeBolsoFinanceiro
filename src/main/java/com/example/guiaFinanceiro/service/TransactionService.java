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
        transaction.setCategory(transactionDto.getCategory());
        transaction.setDescription(transactionDto.getDescription());

        if (transactionDto.getCreditCardId() != null) {
            // Log para debug (verifique no console do Spring)
            System.out.println("Buscando cartão com ID: " + transactionDto.getCreditCardId());

            CreditCard creditCard = creditCardRepository.findById(transactionDto.getCreditCardId())
                    .orElseThrow(() -> new RuntimeException("Cartão com ID " + transactionDto.getCreditCardId() + " não encontrado no banco."));

            // Aqui usamos o método que criamos anteriormente
            Invoice invoice = invoiceRepository.findByCreditCardIdAndPaidFalse(creditCard.getId())
                    .orElseThrow(() -> new RuntimeException("Fatura aberta não encontrada para o cartão: " + creditCard.getName()));

            invoice.setTotalAmount(invoice.getTotalAmount().add(transactionDto.getAmount()));

            BigDecimal novoLimite = creditCard.getAvailableLimit().subtract(transactionDto.getAmount());

            if (novoLimite.compareTo(BigDecimal.ZERO) < 0) {
                throw new RuntimeException("Limite insuficiente! Limite atual: R$ " + creditCard.getAvailableLimit());
            }

            creditCard.setAvailableLimit(novoLimite);
            creditCardRepository.save(creditCard);
            invoiceRepository.save(invoice); // Importante salvar a fatura atualizada

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
    @Transactional
    public BigDecimal purchaseGastoTotal(UUID userId){
        try {
            return transactionRepository.findTotalDebitsByUserId(userId);
        } catch (Exception e) {
            throw new RuntimeException("erro ao processar valores total " + e.getMessage());
        }
    }
    @Transactional
    public Integer GetDiasSemGastos(UUID userId){
        try {
            return transactionRepository.countDaysWithoutExpenses(userId);
        } catch (Exception e) {
            throw new RuntimeException("erro ao processar dias " + e.getMessage());
        }
    }
    @Transactional
    public BigDecimal getGastoConta(UUID userId) {
        try {
        return transactionRepository.sumGastoContaByUserId(userId);
        } catch (Exception e) {
            throw new RuntimeException("erro ao processar gasto conta " + e.getMessage());
        }
    }
    @Transactional
    public BigDecimal getGastoCartao(UUID userId) {
        try {
        return transactionRepository.sumGastoCartaoByUserId(userId);
        } catch (Exception e) {
            throw new RuntimeException("erro ao processar gasto cartao " + e.getMessage());
        }
    }
    @Transactional
    public Double GetRendaMansal(UUID userID){
        try {
            return transactionRepository.getRendaMensalPorUsuario(userID);
        } catch (RuntimeException e) {
            throw new RuntimeException("erro ao processar renda mensal " + e.getMessage());
        }
    }
}
