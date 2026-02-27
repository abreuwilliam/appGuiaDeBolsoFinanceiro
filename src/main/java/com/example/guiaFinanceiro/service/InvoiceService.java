package com.example.guiaFinanceiro.service;

import com.example.guiaFinanceiro.dto.AccountDto;
import com.example.guiaFinanceiro.dto.InvoiceDto;
import com.example.guiaFinanceiro.entites.*;
import com.example.guiaFinanceiro.map.AccountMapper;
import com.example.guiaFinanceiro.map.InvoiceMapper;
import com.example.guiaFinanceiro.repository.AccountRepository;
import com.example.guiaFinanceiro.repository.CreditCardRepository;
import com.example.guiaFinanceiro.repository.InvoiceRepository;
import com.example.guiaFinanceiro.repository.TransactionRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class InvoiceService {

    @Autowired
    private InvoiceRepository invoiceRepository;

    @Autowired
    private CreditCardRepository creditCardRepository;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    @Transactional
    public InvoiceDto createInvoice(InvoiceDto invoiceDto) {

        CreditCard creditCard = creditCardRepository.findById(invoiceDto.getCreditCardId())
                .orElseThrow(() ->
                        new RuntimeException("Cartão não encontrado: " + invoiceDto.getCreditCardId())
                );

        Optional<Invoice> existingInvoice =
                invoiceRepository.findByCreditCardIdAndReferenceMonth(
                        creditCard.getId(),
                        invoiceDto.getReferenceMonth()
                );

        Invoice invoice;

        if (existingInvoice.isPresent()) {

            invoice = existingInvoice.get();

            if (invoice.isPaid()) {
                throw new RuntimeException("Fatura já está paga. Não é possível adicionar valores.");
            }

            invoice.setTotalAmount(
                    invoice.getTotalAmount().add(invoiceDto.getTotalAmount())
            );

        } else {

            invoice = new Invoice();
            invoice.setCreditCard(creditCard);
            LocalDate normalized = invoiceDto.getReferenceMonth().withDayOfMonth(1);
            invoice.setReferenceMonth(normalized);
            invoice.setTotalAmount(invoiceDto.getTotalAmount());
            invoice.setPaid(false);

            creditCard.setAvailableLimit(creditCard.getAvailableLimit().subtract
                    (invoiceDto.getTotalAmount()));
        }

        Invoice saved = invoiceRepository.save(invoice);
        return InvoiceMapper.toDto(saved);
    }

    @Transactional
    public void payInvoice(UUID invoiceId, UUID accountId) {
        Invoice invoice = invoiceRepository.findById(invoiceId)
                .orElseThrow(() -> new EntityNotFoundException("Fatura não encontrada"));

        if (invoice.isPaid()) {
            throw new RuntimeException("Esta fatura já foi paga.");
        }

        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new EntityNotFoundException("Conta bancária não encontrada"));

        if (account.getBalance().compareTo(invoice.getTotalAmount()) < 0) {
            throw new RuntimeException("Saldo insuficiente na conta para pagar esta fatura.");
        }

        // 3. REGISTRA A TRANSAÇÃO DE PAGAMENTO
        Transaction paymentTransaction = new Transaction();
        paymentTransaction.setAmount(invoice.getTotalAmount());
        paymentTransaction.setDate(LocalDate.now());
        paymentTransaction.setType(TransactionType.EXPENSE); // Ou um tipo específico 'PAYMENT'
        paymentTransaction.setDescription("Pagamento fatura: " + invoice.getCreditCard().getName());
        paymentTransaction.setSourceAccount(account); // A conta que pagou

        transactionRepository.save(paymentTransaction);

        // 4. ATUALIZA O SALDO DA CONTA BANCÁRIA
        account.setBalance(account.getBalance().subtract(invoice.getTotalAmount()));
        accountRepository.save(account);

        // 5. MARCA FATURA COMO PAGA E RESTAURA LIMITE DO CARTÃO
        invoice.setPaid(true);
        invoiceRepository.save(invoice);

        CreditCard card = invoice.getCreditCard();
        card.setAvailableLimit(card.getAvailableLimit().add(invoice.getTotalAmount()));
        creditCardRepository.save(card);

        // 6. GERA A PRÓXIMA FATURA
        createNextInvoice(card, invoice.getReferenceMonth());
    }
    private void createNextInvoice(CreditCard card, LocalDate currentMonth) {
        LocalDate nextMonthDate = currentMonth.plusMonths(1);

        // Verifica se já existe uma fatura para o próximo mês para evitar duplicados
        boolean exists = invoiceRepository.existsByCreditCardAndReferenceMonth(card, nextMonthDate);

        if (!exists) {
            Invoice nextInvoice = new Invoice();
            nextInvoice.setReferenceMonth(nextMonthDate);
            nextInvoice.setTotalAmount(BigDecimal.ZERO);
            nextInvoice.setPaid(false);
            nextInvoice.setCreditCard(card);
            invoiceRepository.save(nextInvoice);
        }
    }

    @Transactional
    public List<InvoiceDto> findByUser(UUID userId) {
        InvoiceMapper invoiceMapper = new InvoiceMapper();
        List<Invoice> invoices = invoiceRepository.findByUsers_Id(userId);
        return invoices.stream().map(n -> invoiceMapper.toDto(n)).toList();
    }
    @Transactional
    public InvoiceDto findOpenInvoiceByCard(UUID creditCardId) {
        Invoice invoice = invoiceRepository.findByCreditCardIdAndPaidFalse(creditCardId)
                .orElseThrow(() -> new RuntimeException("Nenhuma fatura aberta encontrada para este cartão."));

        return InvoiceMapper.toDto(invoice);
    }
}