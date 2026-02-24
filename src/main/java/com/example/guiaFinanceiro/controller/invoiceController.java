package com.example.guiaFinanceiro.controller;

import com.example.guiaFinanceiro.dto.InvoiceDto;
import com.example.guiaFinanceiro.service.InvoiceService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/invoice")
public class invoiceController {
    private InvoiceService invoiceService;

    public ResponseEntity<InvoiceDto> postInvoice(InvoiceDto invoiceDto){
        InvoiceDto postInvoice = invoiceService.createInvoice(invoiceDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(postInvoice);
    }
}
