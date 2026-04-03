package com.example.guiaFinanceiro.controller;

import com.example.guiaFinanceiro.dto.AccountDto;
import com.example.guiaFinanceiro.dto.FinancialGoalDto;
import com.example.guiaFinanceiro.service.FinancialGoalService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/financialGoal")
public class FinancialGoalController {

    @Autowired
    private FinancialGoalService financialGoalService;

    @PostMapping
    public ResponseEntity<FinancialGoalDto> create(@Valid @RequestBody FinancialGoalDto financialGoalDto){
        FinancialGoalDto financialGoalDtoCreate = financialGoalService.createFinancialGoal(financialGoalDto);
        return ResponseEntity.ok(financialGoalDtoCreate);
    }
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<FinancialGoalDto>> findByUser(@PathVariable UUID userId){
        List<FinancialGoalDto> contas = financialGoalService.findByUser(userId);
        return ResponseEntity.status(HttpStatus.CREATED).body(contas);
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        financialGoalService.deleteFinancialGoal(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}")
    public ResponseEntity<FinancialGoalDto> update(@PathVariable UUID id, @RequestBody FinancialGoalDto data) {
        FinancialGoalDto updatedFinancialGoal = financialGoalService.updateFinancialGoalPartially(id, data);
        return ResponseEntity.ok(updatedFinancialGoal);
    }
}
