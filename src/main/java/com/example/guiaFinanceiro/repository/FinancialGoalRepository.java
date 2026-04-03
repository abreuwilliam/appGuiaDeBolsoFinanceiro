package com.example.guiaFinanceiro.repository;

import com.example.guiaFinanceiro.entites.Account;
import com.example.guiaFinanceiro.entites.FinancialGoal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface FinancialGoalRepository  extends JpaRepository<FinancialGoal, UUID> {

    List<FinancialGoal> findByUserId(UUID userId);
}
