package com.example.guiaFinanceiro.map;

import com.example.guiaFinanceiro.dto.InvoiceDto;
import com.example.guiaFinanceiro.entites.CreditCard;
import com.example.guiaFinanceiro.entites.Invoice;

public class InvoiceMapper {
    public static InvoiceDto toDto(Invoice invoice){
    if(invoice == null) return null;
    InvoiceDto invoiceDto = new InvoiceDto();
    invoiceDto.setId(invoice.getId());
    invoiceDto.setPaid(invoice.isPaid());
    invoiceDto.setCreditCardId(invoice.getCreditCard().getId());
    invoiceDto.setReferenceMonth(invoice.getReferenceMonth());
    invoiceDto.setTotalAmount(invoice.getTotalAmount());
    return invoiceDto;
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