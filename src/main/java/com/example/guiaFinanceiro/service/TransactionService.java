package com.example.guiaFinanceiro.service;

import com.example.guiaFinanceiro.dto.GastoRecorrenteProjection;
import com.example.guiaFinanceiro.dto.MonthlyHistory;
import com.example.guiaFinanceiro.dto.TransactionDto;
import com.example.guiaFinanceiro.entites.Account;
import com.example.guiaFinanceiro.entites.CreditCard;
import com.example.guiaFinanceiro.entites.Invoice;
import com.example.guiaFinanceiro.entites.Transaction;
import com.example.guiaFinanceiro.map.TransactionMapper;
import com.example.guiaFinanceiro.repository.AccountRepository;
import com.example.guiaFinanceiro.repository.CreditCardRepository;
import com.example.guiaFinanceiro.repository.InvoiceRepository;
import com.example.guiaFinanceiro.repository.TransactionRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class TransactionService {
    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private CreditCardRepository creditCardRepository;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private InvoiceRepository invoiceRepository;

    @Transactional
    public TransactionDto createTransaction(TransactionDto transactionDto) {
        Transaction transaction = new Transaction();
        transaction.setAmount(transactionDto.getAmount());
        transaction.setDate(transactionDto.getDate());
        transaction.setType(transactionDto.getType());
        transaction.setCategory(transactionDto.getCategory());
        transaction.setDescription(transactionDto.getDescription());

        if (transactionDto.getCreditCardId() != null) {
            CreditCard creditCard = creditCardRepository.findById(transactionDto.getCreditCardId())
                    .orElseThrow(() -> new RuntimeException("Cartão não encontrado"));

            BigDecimal amount = transactionDto.getAmount();

            // Normaliza o número de parcelas (mínimo 1)
            Integer installments = (transactionDto.getInstallments() == null || transactionDto.getInstallments() < 1)
                    ? 1
                    : transactionDto.getInstallments();

            // 1. Validação e Atualização do Limite
            BigDecimal novoLimite = creditCard.getAvailableLimit().subtract(amount);
            if (novoLimite.compareTo(BigDecimal.ZERO) < 0) {
                throw new RuntimeException("Limite insuficiente! Limite atual: R$ " + creditCard.getAvailableLimit());
            }

            creditCard.setAvailableLimit(novoLimite);
            creditCardRepository.save(creditCard);

            // 2. Cálculo do valor de cada parcela
            BigDecimal installmentValue = amount.divide(
                    BigDecimal.valueOf(installments),
                    2,
                    RoundingMode.HALF_UP
            );

            // 3. Lógica de busca e desvio para faturas pagas
            // Descobrimos qual o primeiro mês disponível (não pago) para começar as cobranças
            int monthOffset = 0;
            boolean foundAvailableMonth = false;

            while (!foundAvailableMonth) {
                LocalDate checkMonth = LocalDate.now().withDayOfMonth(1).plusMonths(monthOffset);

                Invoice currentCheck = invoiceRepository
                        .findByCreditCardIdAndReferenceMonth(creditCard.getId(), checkMonth)
                        .stream().findFirst().orElse(null);

                // Se a fatura não existe ou existe e NÃO está paga, achamos o mês de início
                if (currentCheck == null || !currentCheck.isPaid()) {
                    foundAvailableMonth = true;
                } else {
                    // Se a fatura existe e está PAGA, tentamos o próximo mês
                    monthOffset++;
                }
            }

            // 4. Loop de Parcelas a partir do mês disponível encontrado
            for (int i = 0; i < installments; i++) {
                LocalDate referenceMonth = LocalDate.now().withDayOfMonth(1).plusMonths(monthOffset + i);

                Invoice invoice = invoiceRepository
                        .findByCreditCardIdAndReferenceMonth(creditCard.getId(), referenceMonth)
                        .stream()
                        .findFirst()
                        .orElseGet(() -> {
                            Invoice newInvoice = new Invoice();
                            newInvoice.setCreditCard(creditCard);
                            newInvoice.setReferenceMonth(referenceMonth);
                            newInvoice.setPaid(false);
                            newInvoice.setTotalAmount(BigDecimal.ZERO);
                            return invoiceRepository.save(newInvoice);
                        });

                // Adiciona o valor da parcela
                invoice.setTotalAmount(invoice.getTotalAmount().add(installmentValue));
                invoiceRepository.save(invoice);
            }

            transaction.setCreditCard(creditCard);
        }

        transactionRepository.save(transaction);

        if (transactionDto.getSourceAccount() != null) {
            Account source = accountRepository.findById(transactionDto.getSourceAccount())
                    .orElseThrow(() -> new RuntimeException("Conta de origem não encontrada"));

            source.setBalance(source.getBalance().subtract(transactionDto.getAmount()));
            accountRepository.save(source);

            transaction.setSourceAccount(source);
        }

        if (transactionDto.getDestinationAccount() != null) {
            Account destination = accountRepository.findById(transactionDto.getDestinationAccount())
                    .orElseThrow(() -> new RuntimeException("Conta de destino não encontrada"));

            destination.setBalance(destination.getBalance().add(transactionDto.getAmount()));
            accountRepository.save(destination);

            transaction.setDestinationAccount(destination);
        }

        try {
            Transaction savedTransaction = transactionRepository.save(transaction);
            return TransactionMapper.toDto(savedTransaction);
        } catch (Exception e) {
            throw new RuntimeException("Erro ao processar transação: " + e.getMessage());
        }
    }

    @Transactional()
    public BigDecimal purchaseGastoTotal(UUID userId) {
        // Gastos em dinheiro/débito (Transaction onde credit_card_id IS NULL)
        BigDecimal gastoConta = transactionRepository.sumGastoContaByUserId(userId);

        // Gastos em parcelas do mês (Invoice onde PAID = false)
        BigDecimal gastoCartao = invoiceRepository.sumGastoCartaoByUserId(userId);

        return gastoConta.add(gastoCartao);
    }

    @Transactional
    public Integer GetDiasSemGastos(UUID userId) {
        try {
            return transactionRepository.countDaysWithoutExpenses(userId);
        } catch (Exception e) {
            throw new RuntimeException("erro ao processar dias " + e.getMessage());
        }
    }

    @Transactional
    public BigDecimal getGastoConta(UUID userId) {
        try {
            return transactionRepository.sumGastoContaByUserId(userId);
        } catch (Exception e) {
            throw new RuntimeException("erro ao processar gasto conta " + e.getMessage());
        }
    }

    @Transactional
    public BigDecimal getGastoCartao(UUID userId) {
        try {
            return transactionRepository.sumGastoCartaoByUserId(userId);
        } catch (Exception e) {
            throw new RuntimeException("erro ao processar gasto cartao " + e.getMessage());
        }
    }

    @Transactional
    public BigDecimal GetRendaMansal(UUID userID) {
        try {
            return transactionRepository.getRendaMensalPorUsuario(userID);
        } catch (RuntimeException e) {
            throw new RuntimeException("erro ao processar renda mensal " + e.getMessage());
        }
    }
    @Transactional
    public Map<String, BigDecimal> getGastosPorCategoria(UUID userId) {
        List<Object[]> resultados = transactionRepository.findGastoPorCategoriaGrouped(userId);
        return resultados.stream().collect(Collectors.toMap(
                line -> (String) line[0],
                line -> (BigDecimal) line[1]
        ));
    }

    @Transactional
    public Map<String, Object> getFinancialHistory(UUID userId) {
        try {
            List<MonthlyHistory> history = transactionRepository.findMonthlyHistoryByUserId(userId);

            Map<String, Object> response = new LinkedHashMap<>();

            // Preencher os últimos 6 meses (incluindo meses sem dados)
            List<Map<String, Object>> monthlyData = fillMissingMonths(history);
            response.put("monthlyData", monthlyData);

            // Análise de tendência
            response.put("trend", analyzeTrend(monthlyData));

            // Média dos gastos
            response.put("averageExpense", calculateAverage(monthlyData, "totalGastos"));

            // Melhor e pior mês
            response.put("bestMonth", findBestMonth(monthlyData));
            response.put("worstMonth", findWorstMonth(monthlyData));

            return response;

        } catch (RuntimeException e) {
            throw new RuntimeException("Erro ao buscar histórico financeiro", e);
        }
    }

    @Transactional
    public List<Map<String, Object>> fillMissingMonths(List<MonthlyHistory> history) {
        List<Map<String, Object>> result = new ArrayList<>();
        LocalDate today = LocalDate.now();

        // Criar mapa dos dados existentes
        Map<String, MonthlyHistory> historyMap = history.stream()
                .collect(Collectors.toMap(
                        h -> h.getAno() + "-" + h.getMes(),
                        h -> h
                ));

        // Preencher últimos 6 meses
        for (int i = 5; i >= 0; i--) {
            LocalDate date = today.minusMonths(i);
            int ano = date.getYear();
            int mes = date.getMonthValue();
            String key = ano + "-" + mes;

            Map<String, Object> monthData = new LinkedHashMap<>();
            monthData.put("ano", ano);
            monthData.put("mes", mes);
            monthData.put("monthYear", String.format("%d/%d", mes, ano));
            monthData.put("monthName", date.getMonth().getDisplayName(TextStyle.SHORT, new Locale("pt", "BR")));

            if (historyMap.containsKey(key)) {
                MonthlyHistory h = historyMap.get(key);
                monthData.put("totalGastos", h.getTotalGastos());
                monthData.put("totalGanhos", h.getTotalGanhos());
                monthData.put("saldoMensal", h.getSaldoMensal());
            } else {
                monthData.put("totalGastos", BigDecimal.ZERO);
                monthData.put("totalGanhos", BigDecimal.ZERO);
                monthData.put("saldoMensal", BigDecimal.ZERO);
            }

            result.add(monthData);
        }

        return result;
    }

    private String analyzeTrend(List<Map<String, Object>> monthlyData) {
        List<BigDecimal> balances = monthlyData.stream()
                .map(m -> (BigDecimal) m.get("saldoMensal"))
                .collect(Collectors.toList());

        // Verificar tendência dos últimos 3 meses
        if (balances.size() >= 3) {
            BigDecimal last = balances.get(balances.size() - 1);
            BigDecimal previous = balances.get(balances.size() - 2);
            BigDecimal older = balances.get(balances.size() - 3);

            if (last.compareTo(previous) > 0 && previous.compareTo(older) > 0) {
                return "MELHORANDO";
            } else if (last.compareTo(previous) < 0 && previous.compareTo(older) < 0) {
                return "PIORANDO";
            } else if (last.compareTo(previous) > 0) {
                return "RECUPERANDO";
            } else if (last.compareTo(previous) < 0) {
                return "DECLINANDO";
            }
        }

        return "ESTÁVEL";
    }

    private BigDecimal calculateAverage(List<Map<String, Object>> data, String field) {
        return data.stream()
                .map(m -> (BigDecimal) m.get(field))
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .divide(BigDecimal.valueOf(data.size()), RoundingMode.HALF_UP);
    }

    private Map<String, Object> findBestMonth(List<Map<String, Object>> data) {
        return data.stream()
                .max(Comparator.comparing(m -> (BigDecimal) m.get("saldoMensal")))
                .orElse(null);
    }

    private Map<String, Object> findWorstMonth(List<Map<String, Object>> data) {
        return data.stream()
                .min(Comparator.comparing(m -> (BigDecimal) m.get("saldoMensal")))
                .orElse(null);
    }
    @Transactional
    public List<String> verificarLimites(UUID userId) {
        try {
            List<String> categoriasCriticas = transactionRepository
                    .findCategoriesExceedingPercentOfIncome(userId, 30.0);
            return categoriasCriticas;
        } catch (RuntimeException e) {
            throw new RuntimeException(" nao posusui categorias"  + e.getMessage());
        }
    }
    @Transactional
    public Double getFinancialHealthScore(UUID userId) {
        return transactionRepository.getHealthScore(userId);
    }
    @Transactional
    public BigDecimal getVariacaoMensal(UUID userId) { return transactionRepository.getVariacaoGastoMensal(userId); }
    @Transactional
    public BigDecimal getMediaGastoDiario(UUID userId) { return transactionRepository.getMediaGastoDiario(userId); }
    @Transactional
    public BigDecimal getPercentualUsoCartao(UUID userId) { return transactionRepository.getPercentualUsoCartao(userId); }
    @Transactional
    public List<GastoRecorrenteProjection> getGastosRecorrentes(UUID userId) {
        try {
            List<GastoRecorrenteProjection> gastos = transactionRepository.findGastosRecorrentes(userId);

            // Log para conferência no console do Spring Boot
            System.out.println("Gastos recorrentes encontrados para o usuário " + userId + ": " + gastos.size());

            return gastos != null ? gastos : new ArrayList<>();
        } catch (Exception e) {
            // Captura o erro para evitar o Erro 500 no Front-end
            System.err.println("CRÍTICO: Erro ao processar gastos recorrentes: " + e.getMessage());
            e.printStackTrace();
            return new ArrayList<>();
        }
    }
}



