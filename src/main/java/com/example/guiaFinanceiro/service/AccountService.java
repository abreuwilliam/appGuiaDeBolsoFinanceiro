package com.example.guiaFinanceiro.service;

import com.example.guiaFinanceiro.dto.AccountDto;
import com.example.guiaFinanceiro.entites.Account;
import com.example.guiaFinanceiro.entites.AccountType;
import com.example.guiaFinanceiro.entites.Users;
import com.example.guiaFinanceiro.map.AccountMapper;
import com.example.guiaFinanceiro.repository.AccountRepository;
import com.example.guiaFinanceiro.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
public class AccountService {

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private UserRepository userRepository;

    @Transactional
    public AccountDto createAccount(AccountDto accountDto) {
        Users user = userRepository.findById(accountDto.getUserId())
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
        Account account = new Account();
        account.setId(accountDto.getId());
        account.setBalance(accountDto.getBalance());
        account.setName(accountDto.getName());
        account.setType(accountDto.getType());
        account.setUsers(user);
        try {
            Account savedAccount = accountRepository.save(account);
            return AccountMapper.toDto(savedAccount);
        } catch (Exception e) {
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

    @Transactional
    public List<AccountDto> findByUser(UUID userId) {
        AccountMapper accountMapper = new AccountMapper();
        List<Account> contas = accountRepository.findByUsers_Id(userId);
        return contas.stream().map(n -> accountMapper.toDto(n)).toList();
    }

    @Transactional
    public void deleteAccount(UUID accountId) {
        if (!accountRepository.existsById(accountId)) {
            throw new RuntimeException("Conta não encontrada para o ID: " + accountId);
        }
        accountRepository.deleteById(accountId);
    }
    @Transactional
    public AccountDto updateAccountPartially(UUID id, AccountDto data) {
        // Busca a conta ou lança erro se não existir
        Account account = accountRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Conta não encontrada"));

        if (account.getType() == AccountType.INVESTMENT &&
                data.getBalance().compareTo(BigDecimal.ZERO) < 0) {
            throw new RuntimeException("Conta de investimento não pode ter saldo negativo");
        }

        // Atualiza apenas os campos que não vierem nulos no DTO
        if (data.getName() != null) {
            account.setName(data.getName());
        }

        if (data.getType() != null) {
            account.setType(data.getType());
        }

        if (data.getBalance() != null) {
            account.setBalance(data.getBalance());
        }

        Account conta = accountRepository.save(account);
        return AccountMapper.toDto(conta);
    }
}

