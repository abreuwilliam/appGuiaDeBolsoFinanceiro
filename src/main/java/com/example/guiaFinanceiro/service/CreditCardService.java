package com.example.guiaFinanceiro.service;

import com.example.guiaFinanceiro.dto.CreditCardDto;
import com.example.guiaFinanceiro.entites.CreditCard;
import com.example.guiaFinanceiro.entites.Users;
import com.example.guiaFinanceiro.map.CreditCardMapper;
import com.example.guiaFinanceiro.repository.CreditCardRepository;
import com.example.guiaFinanceiro.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CreditCardService {
    private CreditCardRepository creditCardRepository;

    @Autowired
    private UserRepository userRepository;

    @Transactional
    public CreditCardDto createCreditCard(CreditCardDto creditCardDto) {
        Users user = userRepository.findById(creditCardDto.getUserId())
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
        CreditCard creditCard = new CreditCard();
        creditCard.setName(creditCardDto.getName());
        creditCard.setUsers(user);
        creditCard.setAvailableLimit(creditCardDto.getAvailableLimit());
        creditCard.setLimitAmount(creditCardDto.getLimitAmount());

        try {
            CreditCard savadCreditCard = creditCardRepository.save(creditCard);
            return CreditCardMapper.toDto(savadCreditCard);
        } catch (Exception e) {
            throw new RuntimeException("Erro ao salvar a conta: " + e.getMessage());
        }
    }
}
