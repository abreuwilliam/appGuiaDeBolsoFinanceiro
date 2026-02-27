package com.example.guiaFinanceiro.controller;

import com.example.guiaFinanceiro.dto.TransactionDto;
import com.example.guiaFinanceiro.service.TransactionService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.UUID;

@RestController
@RequestMapping("/transaction")
public class transactionController {
    @Autowired
    private TransactionService transactionService;

    @PostMapping
    public ResponseEntity<TransactionDto> postTransaction(@RequestBody TransactionDto transactionDto){
        TransactionDto creatTransaction = transactionService.createTransaction(transactionDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(creatTransaction);
    }

    @GetMapping("/gastoTotal/{userId}")
    public ResponseEntity<BigDecimal> getValorTotalGasto(@Valid @PathVariable UUID userId){
        BigDecimal valorTotal = transactionService.purchaseGastoTotal(userId);
        return  ResponseEntity.ok(valorTotal);
    }
    @GetMapping("/diasSemGasto/{userId}")
    public ResponseEntity<Integer> getTotalDiasSemGastos(@Valid @PathVariable UUID userId){
        Integer valorDias = transactionService.GetDiasSemGastos(userId);
        return ResponseEntity.ok(valorDias);
    }
    @GetMapping("/gastoConta/{userId}")
    public ResponseEntity<BigDecimal> getGastoConta(
            @PathVariable UUID userId) {

        BigDecimal total = transactionService.getGastoConta(userId);
        return ResponseEntity.ok(total);
    }


    @GetMapping("/gastoCartao/{userId}")
    public ResponseEntity<BigDecimal> getGastoCartao(
            @PathVariable UUID userId) {

        BigDecimal total = transactionService.getGastoCartao(userId);
        return ResponseEntity.ok(total);
    }

    @GetMapping("/rendaMensal/{userId}")
    public ResponseEntity<Double> getRendaMensal(@Valid @PathVariable UUID userId){
        Double rendaMensal = transactionService.GetRendaMansal(userId);
        return ResponseEntity.ok(rendaMensal);
    }

}
