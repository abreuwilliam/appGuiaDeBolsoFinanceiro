package com.example.guiaFinanceiro.service;

import com.example.guiaFinanceiro.dto.AccountDto;
import com.example.guiaFinanceiro.entites.Account;
import com.example.guiaFinanceiro.entites.Users;
import com.example.guiaFinanceiro.map.AccountMapper;
import com.example.guiaFinanceiro.repository.AccountRepository;
import com.example.guiaFinanceiro.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.UUID;

@Service
public class AccountService {

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private UserRepository userRepository;

    @Transactional
    public AccountDto createAccount(AccountDto accountDto){
        Users user = userRepository.findById(accountDto.getUserId())
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
        Account account = new Account();
        account.setBalance(accountDto.getBalance());
        account.setName(accountDto.getName());
        account.setType(accountDto.getType());
        account.setUsers(user);
        try {
            Account savedAccount =accountRepository.save(account);
            return AccountMapper.toDto(savedAccount);
        }catch (Exception e) {
            throw new RuntimeException("Erro ao salvar a conta: " + e.getMessage());
        }
    }
    @Transactional
    public BigDecimal findBalanceAccount(UUID userId) {
        try {
            return accountRepository.sumBalanceByUserId(userId);
        } catch (Exception e) {
            throw new RuntimeException("Erro ao somar saldos do usuário: " + e.getMessage());
        }
    }
}


