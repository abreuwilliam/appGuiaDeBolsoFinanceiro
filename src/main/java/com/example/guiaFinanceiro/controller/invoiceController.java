package com.example.guiaFinanceiro.controller;

import com.example.guiaFinanceiro.dto.AccountDto;
import com.example.guiaFinanceiro.dto.InvoiceDto;
import com.example.guiaFinanceiro.service.InvoiceService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/invoice")
public class invoiceController {

    @Autowired
    private InvoiceService invoiceService;

    @PostMapping
    public ResponseEntity<InvoiceDto> postInvoice(InvoiceDto invoiceDto){
        InvoiceDto postInvoice = invoiceService.createInvoice(invoiceDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(postInvoice);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<InvoiceDto>> findByUser(@PathVariable UUID userId){
        List<InvoiceDto> invoices = invoiceService.findByUser(userId);
        return ResponseEntity.status(HttpStatus.CREATED).body(invoices);
    }


    @PostMapping("/payment/{invoiceId}/{accountId}")
    public ResponseEntity<Void> paymentInvoice(
            @PathVariable UUID invoiceId,
            @PathVariable UUID accountId
    ) {
        invoiceService.payInvoice(invoiceId, accountId);
        return ResponseEntity.noContent().build();
    }
    @GetMapping("/open/{creditCardId}")
    public ResponseEntity<InvoiceDto> getOpenInvoice(@PathVariable UUID creditCardId) {
        InvoiceDto openInvoice = invoiceService.findOpenInvoiceByCard(creditCardId);
        return ResponseEntity.ok(openInvoice);
    }
}
