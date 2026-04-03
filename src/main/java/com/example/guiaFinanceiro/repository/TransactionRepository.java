package com.example.guiaFinanceiro.repository;

import com.example.guiaFinanceiro.dto.GastoRecorrenteProjection;
import com.example.guiaFinanceiro.dto.MonthlyHistory;
import com.example.guiaFinanceiro.entites.Account;
import com.example.guiaFinanceiro.entites.Invoice;
import com.example.guiaFinanceiro.entites.Transaction;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, UUID> {

    @Query(value = "SELECT COALESCE(SUM(t.AMOUNT), 0) " +
            "FROM TRANSACTION t " +
            "WHERE (t.SOURCE_ACCOUNT_ID IN (SELECT a.ID FROM ACCOUNT a WHERE a.USERS_ID = :userId) " +
            "OR t.CREDIT_CARD_ID IN (SELECT c.ID FROM CREDIT_CARD c WHERE c.USERS_ID = :userId)) " +
            "AND t.TYPE NOT IN ('TRANSFER', 'DEPOSIT', 'INCOME') " +
            "AND MONTH(t.DATE) = MONTH(CURRENT_DATE()) " +
            "AND YEAR(t.DATE) = YEAR(CURRENT_DATE())",
            nativeQuery = true)
    BigDecimal findTotalDebitsByUserId(@Param("userId") UUID userId);

    @Query(value = "SELECT CASE WHEN COUNT(t.id) = 0 THEN 0 " +
            "ELSE DATEDIFF(CURRENT_DATE(), MAX(t.date)) END " +
            "FROM transaction t " +
            "LEFT JOIN account acc ON t.source_account_id = acc.id " +
            "LEFT JOIN credit_card cc ON t.credit_card_id = cc.id " +
            "WHERE (acc.users_id = :userId OR cc.users_id = :userId) " +
            "AND t.type = 'EXPENSE'",
            nativeQuery = true)
    Integer countDaysWithoutExpenses(@Param("userId") UUID userId);

    @Query("SELECT COALESCE(SUM(t.amount), 0) " +
            "FROM Transaction t " +
            "WHERE t.sourceAccount.users.id = :userId " +
            "AND t.creditCard IS NULL " +
            "AND t.type <> 'TRANSFER' " +
            "AND MONTH(t.date) = MONTH(CURRENT_DATE) " +
            "AND YEAR(t.date) = YEAR(CURRENT_DATE)")
    BigDecimal sumGastoContaByUserId(@Param("userId") UUID userId);

    @Query(value = "SELECT COALESCE(SUM(i.TOTAL_AMOUNT), 0) " +
            "FROM INVOICE i " +
            "INNER JOIN CREDIT_CARD cc ON i.CREDIT_CARD_ID = cc.ID " +
            "WHERE cc.USERS_ID = :userId " +
            "AND i.PAID = false " +
            "AND MONTH(i.REFERENCE_MONTH) = MONTH(CURRENT_DATE()) " +
            "AND YEAR(i.REFERENCE_MONTH) = YEAR(CURRENT_DATE())",
            nativeQuery = true)
    BigDecimal sumGastoCartaoByUserId(@Param("userId") UUID userId);

    @Query(value = "SELECT SUM(t.amount) FROM TRANSACTION t " +
            "INNER JOIN ACCOUNT a ON t.destination_account_id = a.id " +
            "WHERE a.users_id = :userId " +
            "AND (t.type = 'INCOME' OR t.type = 'DEPOSIT') " +
            "AND YEAR(t.date) = YEAR(CURRENT_DATE()) " +
            "AND MONTH(t.date) = MONTH(CURRENT_DATE())",
            nativeQuery = true)
    BigDecimal getRendaMensalPorUsuario(@Param("userId") UUID userId);

    @Query(value = "SELECT t.CATEGORY, SUM(t.AMOUNT) " +
            "FROM TRANSACTION t " +
            "LEFT JOIN ACCOUNT acc ON t.SOURCE_ACCOUNT_ID = acc.ID " +
            "LEFT JOIN CREDIT_CARD cc ON t.CREDIT_CARD_ID = cc.ID " +
            "WHERE (acc.USERS_ID = :userId OR cc.USERS_ID = :userId) " +
            "AND t.TYPE IN ('EXPENSE', 'CREDIT_CARD_PURCHASE') " +
            "AND MONTH(t.DATE) = MONTH(CURRENT_DATE()) " +
            "AND YEAR(t.DATE) = YEAR(CURRENT_DATE()) " +
            "GROUP BY t.CATEGORY", nativeQuery = true)
    List<Object[]> findGastoPorCategoriaGrouped(@Param("userId") UUID userId);

    @Query(value = "SELECT " +
            "    res.ano as ano, " +
            "    res.mes as mes, " +
            "    SUM(res.totalGastos) as totalGastos, " +
            "    SUM(res.totalGanhos) as totalGanhos, " +
            "    SUM(res.totalGanhos) - SUM(res.totalGastos) as saldoMensal " +
            "FROM ( " +
            "    /* PARTE 1: Gastos de Conta e Depósitos */ " +
            "    SELECT " +
            "        YEAR(t.DATE) as ano, " +
            "        MONTH(t.DATE) as mes, " +
            "        SUM(CASE WHEN t.TYPE = 'EXPENSE' THEN t.AMOUNT ELSE 0 END) as totalGastos, " +
            "        SUM(CASE WHEN t.TYPE IN ('INCOME', 'DEPOSIT') THEN t.AMOUNT ELSE 0 END) as totalGanhos " +
            "    FROM TRANSACTION t " +
            "    LEFT JOIN ACCOUNT acc_s ON t.SOURCE_ACCOUNT_ID = acc_s.ID " +
            "    LEFT JOIN ACCOUNT acc_d ON t.DESTINATION_ACCOUNT_ID = acc_d.ID " +
            "    WHERE (acc_s.USERS_ID = :userId OR acc_d.USERS_ID = :userId) " +
            "    AND t.DATE >= DATEADD('MONTH', -5, CURRENT_DATE()) " +
            "    GROUP BY YEAR(t.DATE), MONTH(t.DATE) " +
            " " +
            "    UNION ALL " +
            " " +
            "    /* PARTE 2: Gastos de Cartão vindos da Fatura (Invoice) */ " +
            "    SELECT " +
            "        YEAR(i.REFERENCE_MONTH) as ano, " +
            "        MONTH(i.REFERENCE_MONTH) as mes, " +
            "        SUM(i.TOTAL_AMOUNT) as totalGastos, " +
            "        0 as totalGanhos " +
            "    FROM INVOICE i " +
            "    INNER JOIN CREDIT_CARD cc ON i.CREDIT_CARD_ID = cc.ID " +
            "    WHERE cc.USERS_ID = :userId " +
            "    AND i.REFERENCE_MONTH >= DATEADD('MONTH', -5, CURRENT_DATE()) " +
            "    GROUP BY YEAR(i.REFERENCE_MONTH), MONTH(i.REFERENCE_MONTH) " +
            ") res " +
            "GROUP BY res.ano, res.mes " +
            "ORDER BY res.ano DESC, res.mes DESC",
            nativeQuery = true)
    List<MonthlyHistory> findMonthlyHistoryByUserId(@Param("userId") UUID userId);

    @Query(value = "SELECT t.CATEGORY " +
            "FROM TRANSACTION t " +
            "LEFT JOIN ACCOUNT acc ON t.SOURCE_ACCOUNT_ID = acc.ID " +
            "LEFT JOIN CREDIT_CARD cc ON t.CREDIT_CARD_ID = cc.ID " +
            "WHERE (acc.USERS_ID = :userId OR cc.USERS_ID = :userId) " +
            "AND t.TYPE IN ('EXPENSE', 'CREDIT_CARD_PURCHASE') " + // Considera ambos
            "AND MONTH(t.DATE) = MONTH(CURRENT_DATE()) " +
            "AND YEAR(t.DATE) = YEAR(CURRENT_DATE()) " +
            "GROUP BY t.CATEGORY " +
            "HAVING SUM(t.AMOUNT) > (" +
            "   SELECT COALESCE(SUM(income.AMOUNT), 0) * :percentLimit / 100 " +
            "   FROM TRANSACTION income " +
            "   INNER JOIN ACCOUNT a_in ON income.DESTINATION_ACCOUNT_ID = a_in.ID " +
            "   WHERE a_in.USERS_ID = :userId " +
            "   AND (income.TYPE = 'INCOME' OR income.TYPE = 'DEPOSIT') " +
            "   AND MONTH(income.DATE) = MONTH(CURRENT_DATE()) " +
            "   AND YEAR(income.DATE) = YEAR(CURRENT_DATE())" +
            ")",
            nativeQuery = true)
    List<String> findCategoriesExceedingPercentOfIncome(
            @Param("userId") UUID userId,
            @Param("percentLimit") Double percentLimit
    );

    @Query(value = "SELECT " +
            "CASE WHEN income.total_income = 0 THEN 0 " +
            "ELSE (expenses.total_expense / income.total_income) * 100 END " +
            "FROM " +
            "  (SELECT COALESCE(SUM(t.AMOUNT), 0) as total_expense " +
            "   FROM TRANSACTION t " +
            "   LEFT JOIN ACCOUNT acc ON t.SOURCE_ACCOUNT_ID = acc.ID " +
            "   LEFT JOIN CREDIT_CARD cc ON t.CREDIT_CARD_ID = cc.ID " +
            "   WHERE (acc.USERS_ID = :userId OR cc.USERS_ID = :userId) " +
            "   AND t.TYPE IN ('EXPENSE', 'CREDIT_CARD_PURCHASE') " + // <--- MUDANÇA AQUI
            "   AND MONTH(t.DATE) = MONTH(CURRENT_DATE()) " +
            "   AND YEAR(t.DATE) = YEAR(CURRENT_DATE())) expenses, " +
            "  (SELECT COALESCE(SUM(t.AMOUNT), 0) as total_income " +
            "   FROM TRANSACTION t " +
            "   INNER JOIN ACCOUNT acc ON t.DESTINATION_ACCOUNT_ID = acc.ID " +
            "   WHERE acc.USERS_ID = :userId " +
            "   AND (t.TYPE = 'INCOME' OR t.TYPE = 'DEPOSIT') " +
            "   AND MONTH(t.DATE) = MONTH(CURRENT_DATE()) " +
            "   AND YEAR(t.DATE) = YEAR(CURRENT_DATE())) income",
            nativeQuery = true)
    Double getHealthScore(@Param("userId") UUID userId);

    // 6. Variação Mensal (Compara gastos do mês atual vs mês anterior)
    @Query(value = "SELECT " +
            "CASE WHEN mes_anterior = 0 THEN 0 " +
            "ELSE ((mes_atual - mes_anterior) / mes_anterior) * 100 END " +
            "FROM ( " +
            "  SELECT " +
            "    COALESCE(SUM(CASE WHEN MONTH(t.DATE) = MONTH(CURRENT_DATE()) AND YEAR(t.DATE) = YEAR(CURRENT_DATE()) THEN t.AMOUNT ELSE 0 END), 0) as mes_atual, " +
            "    COALESCE(SUM(CASE WHEN MONTH(t.DATE) = MONTH(DATE_SUB(CURRENT_DATE(), INTERVAL 1 MONTH)) AND YEAR(t.DATE) = YEAR(DATE_SUB(CURRENT_DATE(), INTERVAL 1 MONTH)) THEN t.AMOUNT ELSE 0 END), 0) as mes_anterior " +
            "  FROM TRANSACTION t " +
            "  LEFT JOIN ACCOUNT acc ON t.SOURCE_ACCOUNT_ID = acc.ID " +
            "  LEFT JOIN CREDIT_CARD cc ON t.CREDIT_CARD_ID = cc.ID " +
            "  WHERE (acc.USERS_ID = :userId OR cc.USERS_ID = :userId) " +
            "  AND t.TYPE = 'EXPENSE' " +
            ") as comparativo", nativeQuery = true)
    BigDecimal getVariacaoGastoMensal(@Param("userId") UUID userId);

    // 7. Média de Gasto Diário (Total do mês / Dias passados no mês)
    @Query(value = "SELECT COALESCE(SUM(t.AMOUNT), 0) / DAY(CURRENT_DATE()) " +
            "FROM TRANSACTION t " +
            "LEFT JOIN ACCOUNT acc ON t.SOURCE_ACCOUNT_ID = acc.ID " +
            "LEFT JOIN CREDIT_CARD cc ON t.CREDIT_CARD_ID = cc.ID " +
            "WHERE (acc.USERS_ID = :userId OR cc.USERS_ID = :userId) " +
            "AND t.TYPE = 'EXPENSE' " +
            "AND MONTH(t.DATE) = MONTH(CURRENT_DATE()) " +
            "AND YEAR(t.DATE) = YEAR(CURRENT_DATE())", nativeQuery = true)
    BigDecimal getMediaGastoDiario(@Param("userId") UUID userId);

    // 8. Percentual de Uso de Cartão (Gasto Cartão / Gasto Total)
    @Query(value = "SELECT " +
            "CASE WHEN total.total_geral = 0 THEN 0 " +
            "ELSE (cartao.total_cartao / total.total_geral) * 100 END " +
            "FROM ( " +
            "  SELECT COALESCE(SUM(t.AMOUNT), 0) as total_geral " +
            "  FROM TRANSACTION t " +
            "  LEFT JOIN ACCOUNT acc ON t.SOURCE_ACCOUNT_ID = acc.ID " +
            "  LEFT JOIN CREDIT_CARD cc ON t.CREDIT_CARD_ID = cc.ID " +
            "  WHERE (acc.USERS_ID = :userId OR cc.USERS_ID = :userId) " +
            "  AND t.TYPE IN ('EXPENSE', 'CREDIT_CARD_PURCHASE') " + // Soma TUDO que saiu
            "  AND MONTH(t.DATE) = MONTH(CURRENT_DATE()) " +
            "  AND YEAR(t.DATE) = YEAR(CURRENT_DATE()) " +
            ") total, " +
            "( " +
            "  SELECT COALESCE(SUM(t.AMOUNT), 0) as total_cartao " +
            "  FROM TRANSACTION t " +
            "  INNER JOIN CREDIT_CARD cc ON t.CREDIT_CARD_ID = cc.ID " +
            "  WHERE cc.USERS_ID = :userId " +
            "  AND t.TYPE = 'CREDIT_CARD_PURCHASE' " + // Filtra apenas o que é CARTÃO
            "  AND MONTH(t.DATE) = MONTH(CURRENT_DATE()) " +
            "  AND YEAR(t.DATE) = YEAR(CURRENT_DATE()) " +
            ") cartao", nativeQuery = true)
    BigDecimal getPercentualUsoCartao(@Param("userId") UUID userId);

    @Query(value = "SELECT t.ID as id, t.AMOUNT as amount, t.CATEGORY as category, " +
            "t.DESCRIPTION as description, t.DATE as date, t.TYPE as type " +
            "FROM TRANSACTION t " +
            "LEFT JOIN ACCOUNT acc ON t.SOURCE_ACCOUNT_ID = acc.ID " +
            "LEFT JOIN CREDIT_CARD cc ON t.CREDIT_CARD_ID = cc.ID " +
            "WHERE (acc.USERS_ID = :userId OR cc.USERS_ID = :userId) " +
            "AND t.TYPE IN ('EXPENSE', 'CREDIT_CARD_PURCHASE') " +
            "AND t.CATEGORY IN ( " +
            "    SELECT sub.CATEGORY FROM TRANSACTION sub " +
            "    LEFT JOIN ACCOUNT a2 ON sub.SOURCE_ACCOUNT_ID = a2.ID " +
            "    LEFT JOIN CREDIT_CARD c2 ON sub.CREDIT_CARD_ID = c2.ID " +
            "    WHERE (a2.USERS_ID = :userId OR c2.USERS_ID = :userId) " +
            "    AND sub.DATE >= DATEADD('MONTH', -3, CURRENT_DATE()) " +
            "    GROUP BY sub.CATEGORY HAVING COUNT(DISTINCT MONTH(sub.DATE)) >= 2 " +
            ") " +
            "AND MONTH(t.DATE) = MONTH(CURRENT_DATE()) " +
            "AND YEAR(t.DATE) = YEAR(CURRENT_DATE())", nativeQuery = true)
    List<GastoRecorrenteProjection> findGastosRecorrentes(@Param("userId") UUID userId);

    @Modifying
    @Transactional
    @Query("DELETE FROM Transaction t WHERE t.sourceAccount.id = :accountId OR t.destinationAccount.id = :accountId")
    void deleteAllByAccountId(@Param("accountId") UUID accountId);

}