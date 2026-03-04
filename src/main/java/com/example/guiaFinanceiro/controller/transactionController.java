package com.example.guiaFinanceiro.controller;

import com.example.guiaFinanceiro.dto.GastoRecorrenteProjection;
import com.example.guiaFinanceiro.dto.TransactionDto;
import com.example.guiaFinanceiro.entites.Transaction;
import com.example.guiaFinanceiro.service.TransactionService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
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

    @GetMapping("/gastoConta/{userId}")
    public ResponseEntity<BigDecimal> getGastoConta(
            @PathVariable UUID userId) {

        BigDecimal total = transactionService.getGastoConta(userId);
        return ResponseEntity.ok(total);
    }

    @GetMapping("/diasSemGasto/{userId}")
    public ResponseEntity<Integer> getTotalDiasSemGastos(@Valid @PathVariable UUID userId){
        Integer valorDias = transactionService.GetDiasSemGastos(userId);
        return ResponseEntity.ok(valorDias);
    }

    @GetMapping("/gastoCartao/{userId}")
    public ResponseEntity<BigDecimal> getGastoCartao(
            @PathVariable UUID userId) {

        BigDecimal total = transactionService.getGastoCartao(userId);
        return ResponseEntity.ok(total);
    }

    @GetMapping("/rendaMensal/{userId}")
    public ResponseEntity<BigDecimal> getRendaMensal(@Valid @PathVariable UUID userId){
        BigDecimal rendaMensal = transactionService.GetRendaMansal(userId);
        return ResponseEntity.ok(rendaMensal);
    }
    @GetMapping("/gastoPorCategoria/{userId}")
    public ResponseEntity<Map<String, BigDecimal>> getGastoPorCategoria(@PathVariable UUID userId) {
        Map<String, BigDecimal> gastos = transactionService.getGastosPorCategoria(userId);
        return ResponseEntity.ok(gastos);

    }
    @GetMapping("/history/{userId}")
    public ResponseEntity<Map<String, Object>> getFinancialHistory(@PathVariable UUID userId) {
        Map<String, Object> history = transactionService.getFinancialHistory(userId);
        return ResponseEntity.ok(history);
    }

    @GetMapping("/alerts/limits/{userId}")
    public ResponseEntity<List<String>> getCategoriasCriticas(@PathVariable UUID userId) {
        // Chamando o método que você criou
        List<String> categorias = transactionService.verificarLimites(userId);

        if (categorias.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(categorias);
    }

    @GetMapping("/health-score/{userId}")
    public ResponseEntity<Double> getHealthScore(@PathVariable UUID userId) {
        Double score = transactionService.getFinancialHealthScore(userId);
        return ResponseEntity.ok(score);
        //o front você mostra:
        //🟢 Até 50% → Saudável🟡 50–80% → Atenção🔴 Acima de 80% → Risco
    }
    @RestController
    @RequestMapping("/api/transactions")
    public class TransactionController {

        @Autowired
        private TransactionService transactionService;

        @GetMapping("/variacaoMensal/{userId}")
        public ResponseEntity<BigDecimal> getVariacaoMensal(@PathVariable UUID userId) {
            return ResponseEntity.ok(transactionService.getVariacaoMensal(userId));
        }


        @GetMapping("/mediaGastoDiario/{userId}")
        public ResponseEntity<BigDecimal> getMediaGastoDiario(@PathVariable UUID userId) {
            return ResponseEntity.ok(transactionService.getMediaGastoDiario(userId));
        }

        @GetMapping("/percentualCartao/{userId}")
        public ResponseEntity<BigDecimal> getPercentualUsoCartao(@PathVariable UUID userId) {
            return ResponseEntity.ok(transactionService.getPercentualUsoCartao(userId));
        }

        @GetMapping("/gastosRecorrentes/{userId}")
        public ResponseEntity<List<GastoRecorrenteProjection>>getGastosRecorrentes(@PathVariable UUID userId) {
            List<GastoRecorrenteProjection>transactions = transactionService.getGastosRecorrentes(userId);
            return ResponseEntity.ok(transactions);
        }
    }
}
