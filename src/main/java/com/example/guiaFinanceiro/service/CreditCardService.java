package com.example.guiaFinanceiro.service;

import com.example.guiaFinanceiro.dto.CreditCardDto;
import com.example.guiaFinanceiro.entites.CreditCard;
import com.example.guiaFinanceiro.map.CreditCardMapper;
import com.example.guiaFinanceiro.repository.CreditCardRepository;
import org.springframework.stereotype.Service;

@Service
public class CreditCardService {
    private CreditCardRepository creditCardRepository;

    public CreditCardDto createCreditCard(CreditCardDto creditCardDto) {
        CreditCard creditCard = new CreditCard();
        creditCard.setName(creditCardDto.getName());
        creditCard.setUsers(creditCardDto.getUsers());
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
