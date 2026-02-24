package com.example.guiaFinanceiro.controller;

import com.example.guiaFinanceiro.dto.CreditCardDto;
import com.example.guiaFinanceiro.service.CreditCardService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/CreditCard")
public class CreditCardController {
    private CreditCardService creditCardService;

    @PostMapping
    public ResponseEntity<CreditCardDto> postCreditCard(CreditCardDto creditCardDto){
        CreditCardDto creatCreditCardDto = creditCardService.createCreditCard(creditCardDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(creatCreditCardDto);
    }
}
