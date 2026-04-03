package com.example.guiaFinanceiro.map;

import com.example.guiaFinanceiro.dto.CreditCardDto;
import com.example.guiaFinanceiro.dto.FinancialGoalDto;
import com.example.guiaFinanceiro.entites.CreditCard;
import com.example.guiaFinanceiro.entites.FinancialGoal;

public class FinancialGoalMapper {
    public static FinancialGoalDto toDto(FinancialGoal financialGoal) {
        if (financialGoal == null) return null;
        FinancialGoalDto financialGoalDto = new FinancialGoalDto();
        if (financialGoal.getId() != null) {
            financialGoalDto.setId(financialGoal.getId());
        }
        financialGoalDto.setName(financialGoal.getName());
        financialGoalDto.setCompleted(financialGoal.isCompleted());
        financialGoalDto.setUser(financialGoal.getUser().getId());
        financialGoalDto.setCurrentAmount(financialGoal.getCurrentAmount());
        financialGoalDto.setTargetAmount(financialGoal.getTargetAmount());
        financialGoalDto.setTargetDate(financialGoal.getTargetDate());
        return financialGoalDto;
    }
}

