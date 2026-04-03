package com.example.guiaFinanceiro.controller;

import com.example.guiaFinanceiro.service.IaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/Ia")
public class IaController {
    @Autowired
    private IaService iaService;

    @GetMapping("/{id}")
    public ResponseEntity<Map<String,String>> getAdvice(@PathVariable UUID id){
        Map<String,String> iaService1 = iaService.getAdvice(id);
        return ResponseEntity.ok(iaService1);
    }

    @GetMapping("/dicas/{id}")
    public ResponseEntity<String> getDicas(@PathVariable UUID id){
        String dicas = iaService.analyzeUserFinances(id);
        return ResponseEntity.ok(dicas);
    }


}
