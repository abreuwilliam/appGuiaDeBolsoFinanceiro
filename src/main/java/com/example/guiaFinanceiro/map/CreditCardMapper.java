package com.example.guiaFinanceiro.map;


import com.example.guiaFinanceiro.dto.CreditCardDto;
import com.example.guiaFinanceiro.entites.CreditCard;

public class CreditCardMapper {
    public static CreditCardDto toDto(CreditCard creditCard) {
        if (creditCard == null) return null;
        CreditCardDto creditCardDto = new CreditCardDto();
        creditCardDto.setLimitAmount(creditCard.getLimitAmount());
        creditCardDto.setName(creditCard.getName());
        creditCardDto.setAvailableLimit(creditCard.getAvailableLimit());
        creditCardDto.setUserId(creditCard.getUsers().getId());
        return creditCardDto;
    }
}