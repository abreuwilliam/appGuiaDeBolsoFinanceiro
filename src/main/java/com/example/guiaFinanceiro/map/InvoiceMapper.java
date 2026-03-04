package com.example.guiaFinanceiro.map;

import com.example.guiaFinanceiro.dto.InvoiceDto;
import com.example.guiaFinanceiro.entites.CreditCard;
import com.example.guiaFinanceiro.entites.Invoice;

public class InvoiceMapper {
    public static InvoiceDto toDto(Invoice invoice) {
        InvoiceDto dto = new InvoiceDto();
        dto.setId(invoice.getId());
        dto.setTotalAmount(invoice.getTotalAmount());
        dto.setPaid(invoice.isPaid());
        dto.setReferenceMonth(invoice.getReferenceMonth());

        // CORREÇÃO: Só pega o ID se o cartão não for nulo
        if (invoice.getCreditCard() != null) {
            dto.setCreditCardId(invoice.getCreditCard().getId());
        } else {
            dto.setCreditCardId(null);
        }

        return dto;
    }
    public static Invoice toEntity(InvoiceDto invoiceDto, CreditCard creditCard){
        if(invoiceDto == null) return null;
        Invoice invoice = new Invoice();
        invoice.setPaid(invoiceDto.isPaid());
        invoice.setCreditCard(creditCard);
        invoice.setReferenceMonth(invoiceDto.getReferenceMonth());
        invoice.setTotalAmount(invoiceDto.getTotalAmount());
        return invoice;
    }
}