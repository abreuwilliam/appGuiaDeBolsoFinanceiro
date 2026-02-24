package com.example.guiaFinanceiro.service;

import com.example.guiaFinanceiro.dto.InvoiceDto;
import com.example.guiaFinanceiro.entites.CreditCard;
import com.example.guiaFinanceiro.entites.Invoice;
import com.example.guiaFinanceiro.map.InvoiceMapper;
import com.example.guiaFinanceiro.repository.CreditCardRepository;
import com.example.guiaFinanceiro.repository.InvoiceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class InvoiceService {
    @Autowired
    private InvoiceRepository invoiceRepository;

    @Autowired
    private CreditCardRepository creditCardRepository;

    public InvoiceDto createInvoice(InvoiceDto invoiceDto){
        CreditCard creditCard = creditCardRepository.findById(invoiceDto.getCreditCardId())
                .orElseThrow(() -> new RuntimeException("Cartão de Crédito não encontrado com o ID: " + invoiceDto.getCreditCardId()));
        Invoice invoice = new Invoice();
        invoice.setCreditCard(creditCard);
        invoice.setPaid(invoiceDto.isPaid());
        invoice.setTotalAmount(invoiceDto.getTotalAmount());
        invoice.setReferenceMonth(invoiceDto.getReferenceMonth());

        try {
            Invoice savedInvoice = invoiceRepository.save(invoice);
            return InvoiceMapper.toDto(savedInvoice);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
