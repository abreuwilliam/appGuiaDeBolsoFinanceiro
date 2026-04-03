package com.example.guiaFinanceiro.service;

import com.example.guiaFinanceiro.dto.AccountDto;
import com.example.guiaFinanceiro.dto.FinancialGoalDto;
import com.example.guiaFinanceiro.entites.Account;
import com.example.guiaFinanceiro.entites.AccountType;
import com.example.guiaFinanceiro.entites.FinancialGoal;
import com.example.guiaFinanceiro.entites.Users;
import com.example.guiaFinanceiro.map.AccountMapper;
import com.example.guiaFinanceiro.map.FinancialGoalMapper;
import com.example.guiaFinanceiro.repository.FinancialGoalRepository;
import com.example.guiaFinanceiro.repository.TransactionRepository;
import com.example.guiaFinanceiro.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
public class FinancialGoalService {
    @Autowired
    private FinancialGoalRepository financialGoalRepository;
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    @Transactional
    public FinancialGoalDto createFinancialGoal(FinancialGoalDto financialGoalDto) {
        Users users = userRepository.findById(financialGoalDto.getUser())
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
        FinancialGoal financialGoal = new FinancialGoal();
        financialGoal.setName(financialGoalDto.getName());
        financialGoal.setTargetAmount(financialGoalDto.getTargetAmount());
        financialGoal.setTargetDate(financialGoalDto.getTargetDate());
        financialGoal.setCompleted(false);
        financialGoal.setCurrentAmount(financialGoalDto.getCurrentAmount());
        financialGoal.setUser(users);
        try {
            FinancialGoal financialGoalSave = financialGoalRepository.save(financialGoal);
            return FinancialGoalMapper.toDto(financialGoalSave);

        } catch (Exception e) {
            throw new RuntimeException("Erro ao salvar a meta financeira: " + e.getMessage());
        }
    }

    @Transactional
    public List<FinancialGoalDto> findByUser(UUID userId) {
        FinancialGoalMapper financialGoalMapper = new FinancialGoalMapper();
        List<FinancialGoal> contas = financialGoalRepository.findByUserId(userId);
        return contas.stream().map(n -> financialGoalMapper.toDto(n)).toList();
    }

    @Transactional
    public void deleteFinancialGoal(UUID fincialGoalId) {

        FinancialGoal financialGoal = financialGoalRepository.findById(fincialGoalId).orElseThrow(() -> new EntityNotFoundException("Conta não encontrada"));

        transactionRepository.deleteAllByAccountId(fincialGoalId);

        if (!financialGoalRepository.existsById(fincialGoalId)) {
            throw new RuntimeException("Conta não encontrada para o ID: " + fincialGoalId);
        }
        financialGoalRepository.deleteById(fincialGoalId);
    }

    @Transactional
    public FinancialGoalDto updateFinancialGoalPartially(UUID id, FinancialGoalDto data) {

        FinancialGoal financialGoal = financialGoalRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Objetivo não encontrado"));

        if (financialGoal.isCompleted()) {
            throw new RuntimeException("Um objetivo concluído não pode ser alterado.");
        }

        if (data.getName() != null) {
            financialGoal.setName(data.getName());
        }

        if (data.getTargetDate() != null) {
            financialGoal.setTargetDate(data.getTargetDate());
        }

        // Atualizar targetAmount com validação
        if (data.getTargetAmount() != null) {

            if (data.getTargetAmount().compareTo(financialGoal.getCurrentAmount()) < 0) {
                throw new RuntimeException("A meta não pode ser menor que o valor já acumulado.");
            }

            financialGoal.setTargetAmount(data.getTargetAmount());
        }

        // Atualizar currentAmount com validação
        if (data.getCurrentAmount() != null) {

            if (data.getCurrentAmount().compareTo(BigDecimal.ZERO) < 0) {
                throw new RuntimeException("O saldo do objetivo não pode ser negativo.");
            }

            if (data.getCurrentAmount().compareTo(financialGoal.getTargetAmount()) > 0) {
                throw new RuntimeException("O valor atual não pode ultrapassar a meta.");
            }

            financialGoal.setCurrentAmount(data.getCurrentAmount());
        }

        // Verificar se a meta foi concluída
        if (financialGoal.getCurrentAmount().compareTo(financialGoal.getTargetAmount()) == 0) {
            financialGoal.setCompleted(true);
        }

        FinancialGoal updatedGoal = financialGoalRepository.save(financialGoal);

        return FinancialGoalMapper.toDto(updatedGoal);
    }
}