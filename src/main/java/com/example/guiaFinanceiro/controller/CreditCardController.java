package com.example.guiaFinanceiro.controller;

import com.example.guiaFinanceiro.dto.AccountDto;
import com.example.guiaFinanceiro.dto.CreditCardDto;
import com.example.guiaFinanceiro.service.CreditCardService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/CreditCard")
public class CreditCardController {
    @Autowired
    private CreditCardService creditCardService;

    @PostMapping
    public ResponseEntity<CreditCardDto> postCreditCard(@Valid @RequestBody CreditCardDto creditCardDto){
        CreditCardDto creatCreditCardDto = creditCardService.createCreditCard(creditCardDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(creatCreditCardDto);
    }
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<CreditCardDto>> getByCreditCard(@Valid @PathVariable UUID userId){
        List<CreditCardDto> cartoes = creditCardService.findByUser(userId);
        return ResponseEntity.ok(cartoes);
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        creditCardService.deleteCreditCard(id);
        return ResponseEntity.noContent().build();
    }
    @PatchMapping("/{id}")
    public ResponseEntity<CreditCardDto> update(@PathVariable UUID id, @RequestBody CreditCardDto data) {
        CreditCardDto updatedCreditCard = creditCardService.updateCreditCardPartially(id, data);
        return ResponseEntity.ok(updatedCreditCard);
    }
}
