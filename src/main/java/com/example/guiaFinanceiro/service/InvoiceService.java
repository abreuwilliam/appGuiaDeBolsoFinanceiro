package com.example.guiaFinanceiro.service;

import com.example.guiaFinanceiro.dto.InvoiceDto;
import com.example.guiaFinanceiro.entites.Invoice;
import com.example.guiaFinanceiro.map.InvoiceMapper;
import com.example.guiaFinanceiro.repository.InvoiceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class InvoiceService {
    @Autowired
    private InvoiceRepository invoiceRepository;

    public InvoiceDto createInvoice(InvoiceDto invoiceDto){
        Invoice invoice = new Invoice();
        invoice.setCreditCard(invoiceDto.getCreditCard());
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
