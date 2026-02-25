package com.example.guiaFinanceiro.service;

import com.example.guiaFinanceiro.dto.InvoiceDto;
import com.example.guiaFinanceiro.entites.CreditCard;
import com.example.guiaFinanceiro.entites.Invoice;
import com.example.guiaFinanceiro.map.InvoiceMapper;
import com.example.guiaFinanceiro.repository.CreditCardRepository;
import com.example.guiaFinanceiro.repository.InvoiceRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Optional;
@Service
public class InvoiceService {

    @Autowired
    private InvoiceRepository invoiceRepository;

    @Autowired
    private CreditCardRepository creditCardRepository;

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
}