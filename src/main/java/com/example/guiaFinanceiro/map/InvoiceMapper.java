package com.example.guiaFinanceiro.map;

import com.example.guiaFinanceiro.dto.InvoiceDto;
import com.example.guiaFinanceiro.entites.Invoice;

public class InvoiceMapper {
    public static InvoiceDto toDto(Invoice invoice){
    if(invoice == null) return null;
    InvoiceDto invoiceDto = new InvoiceDto();
    invoiceDto.setPaid(invoice.isPaid());
    invoiceDto.setCreditCardId(invoice.getCreditCard().getId());
    invoiceDto.setReferenceMonth(invoice.getReferenceMonth());
    invoiceDto.setTotalAmount(invoice.getTotalAmount());
    return invoiceDto;
    }
}