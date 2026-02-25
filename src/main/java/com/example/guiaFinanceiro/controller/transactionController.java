package com.example.guiaFinanceiro.controller;

import com.example.guiaFinanceiro.dto.TransactionDto;
import com.example.guiaFinanceiro.service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/transaction")
public class transactionController {
    @Autowired
    private TransactionService transactionService;

    @PostMapping
    public ResponseEntity<TransactionDto> postTransaction(TransactionDto transactionDto){
        TransactionDto creatTransaction = transactionService.createTransaction(transactionDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(creatTransaction);
    }
}
