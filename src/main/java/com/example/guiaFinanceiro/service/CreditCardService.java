package com.example.guiaFinanceiro.service;

import com.example.guiaFinanceiro.dto.AccountDto;
import com.example.guiaFinanceiro.dto.CreditCardDto;
import com.example.guiaFinanceiro.dto.InvoiceDto;
import com.example.guiaFinanceiro.entites.*;
import com.example.guiaFinanceiro.map.AccountMapper;
import com.example.guiaFinanceiro.map.CreditCardMapper;
import com.example.guiaFinanceiro.map.InvoiceMapper;
import com.example.guiaFinanceiro.repository.CreditCardRepository;
import com.example.guiaFinanceiro.repository.InvoiceRepository;
import com.example.guiaFinanceiro.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
public class CreditCardService {
    @Autowired
    private CreditCardRepository creditCardRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private InvoiceService invoiceService;

    @Autowired
    private InvoiceRepository invoiceRepository;

    @Transactional
    public CreditCardDto createCreditCard(CreditCardDto creditCardDto) {
        Users user = userRepository.findById(creditCardDto.getUserId())
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
        CreditCard creditCard = new CreditCard();
        creditCard.setName(creditCardDto.getName());
        creditCard.setUsers(user);
        creditCard.setAvailableLimit(creditCardDto.getLimitAmount());
        creditCard.setLimitAmount(creditCardDto.getLimitAmount());

        try {
            CreditCard savadCreditCard = creditCardRepository.save(creditCard);

            Invoice invoice = new Invoice();
            invoice.setReferenceMonth(LocalDate.now().withDayOfMonth(1));
            invoice.setTotalAmount(BigDecimal.ZERO);
            invoice.setPaid(false);
            invoice.setCreditCard(savadCreditCard);
            InvoiceDto invoiceDto = InvoiceMapper.toDto(invoice);
            invoiceService.createInvoice(invoiceDto);

            return CreditCardMapper.toDto(savadCreditCard);
        } catch (Exception e) {
            throw new RuntimeException("Erro ao salvar a conta: " + e.getMessage());
        }
    }
    public List<CreditCardDto> findByUser(UUID userId){
        try {
            List<CreditCard> cartoes = creditCardRepository.findByUsers_Id(userId);
            return cartoes.stream().map(n -> CreditCardMapper.toDto(n)).toList();
        } catch (RuntimeException e) {
            throw new RuntimeException("erro ao retornar cartoes" + e.getMessage());
        }
    }

    @Transactional
    public void deleteCreditCard(UUID creditCardId) {
        // 1. Busca o cartão e suas faturas
        CreditCard creditCard = creditCardRepository.findById(creditCardId)
                .orElseThrow(() -> new RuntimeException("Cartão não encontrado para o ID: " + creditCardId));

        // 2. Busca todas as faturas associadas a este cartão
        List<Invoice> invoices = invoiceRepository.findByCreditCardId(creditCardId).orElseThrow(() -> new EntityNotFoundException("Conta não encontrada"));

        // 3. A TRAVA: Verifica se existe alguma fatura com valor maior que zero
        // Isso garante que se houver histórico de gastos, o cartão não seja apagado
        boolean temGastos = invoices.stream()
                .anyMatch(invoice -> invoice.getTotalAmount().compareTo(BigDecimal.ZERO) > 0);

        if (temGastos) {
            throw new RuntimeException("Não é possível apagar um cartão que possui faturas com gastos registrados. Considere desativá-lo.");
        }

        // 4. Se o valor total de todas as faturas for 0, apaga as faturas vazias primeiro
        invoiceRepository.deleteAll(invoices);

        // 5. Por fim, apaga o cartão
        creditCardRepository.delete(creditCard);
    }

    @Transactional
    public CreditCardDto updateCreditCardPartially(UUID id, CreditCardDto data) {
        // Busca a conta ou lança erro se não existir
        CreditCard creditCard = creditCardRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Conta não encontrada"));

        // Atualiza apenas os campos que não vierem nulos no DTO
        if (data.getAvailableLimit() != null) {
            creditCard.setAvailableLimit(data.getAvailableLimit());
        }

        if (data.getName() != null) {
            creditCard.setName(data.getName());
        }

        if (data.getLimitAmount() != null) {
            creditCard.setLimitAmount(data.getLimitAmount());
        }

        CreditCard cartao = creditCardRepository.save(creditCard);
        return CreditCardMapper.toDto(cartao);
    }
}
