package com.example.guiaFinanceiro.repository;

import com.example.guiaFinanceiro.entites.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.UUID;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, UUID> {

    @Query(value = "SELECT COALESCE(SUM(t.AMOUNT), 0) " +
            "FROM TRANSACTION t " +
            "WHERE (t.SOURCE_ACCOUNT_ID IN (SELECT a.ID FROM ACCOUNT a WHERE a.USERS_ID = :userId) " +
            "   OR t.CREDIT_CARD_ID IN (SELECT c.ID FROM CREDIT_CARD c WHERE c.USERS_ID = :userId)) " +
            "AND t.DATE >= DATE_TRUNC('MONTH', CURRENT_DATE())",
            nativeQuery = true)
    BigDecimal findTotalDebitsByUserId(@Param("userId") UUID userId);

    @Query(value = "SELECT CASE WHEN COUNT(t.ID) = 0 THEN 0 " +
            "ELSE DATEDIFF('DAY', MAX(t.DATE), CURRENT_DATE()) END " +
            "FROM TRANSACTION t " +
            "LEFT JOIN ACCOUNT acc ON t.SOURCE_ACCOUNT_ID = acc.ID " +
            "LEFT JOIN CREDIT_CARD cc ON t.CREDIT_CARD_ID = cc.ID " +
            "WHERE (acc.USERS_ID = :userId OR cc.USERS_ID = :userId) " +
            "AND t.TYPE = 'EXPENSE'",
            nativeQuery = true)
    Integer countDaysWithoutExpenses(@Param("userId") UUID userId);

    // 🔵 GASTOS EM CONTA
    @Query(value = "SELECT COALESCE(SUM(t.AMOUNT), 0) " +
            "FROM TRANSACTION t " +
            "INNER JOIN ACCOUNT acc ON t.SOURCE_ACCOUNT_ID = acc.ID " +
            "WHERE CAST(acc.USERS_ID AS VARCHAR) = CAST(:userId AS VARCHAR) " +
            "AND t.CREDIT_CARD_ID IS NULL " +
            "AND MONTH(t.DATE) = MONTH(CURRENT_DATE()) " +
            "AND YEAR(t.DATE) = YEAR(CURRENT_DATE())",
            nativeQuery = true)
    BigDecimal sumGastoContaByUserId(@Param("userId") UUID userId);

    // 🟣 GASTOS EM CARTÃO (Tudo que passou pelo cartão)
    @Query(value = "SELECT COALESCE(SUM(t.AMOUNT), 0) " +
            "FROM TRANSACTION t " +
            "INNER JOIN CREDIT_CARD cc ON t.CREDIT_CARD_ID = cc.ID " +
            "WHERE CAST(cc.USERS_ID AS VARCHAR) = CAST(:userId AS VARCHAR) " +
            "AND MONTH(t.DATE) = MONTH(CURRENT_DATE()) " +
            "AND YEAR(t.DATE) = YEAR(CURRENT_DATE())",
            nativeQuery = true)
    BigDecimal sumGastoCartaoByUserId(@Param("userId") UUID userId);

    @Query(value = "SELECT SUM(t.amount) FROM TRANSACTION t " +
            "INNER JOIN ACCOUNT a ON t.destination_account_id = a.id " +
            "WHERE a.users_id = :userId " +
            "AND (t.type = 'INCOME' OR t.type = 'DEPOSIT') " +
            "AND YEAR(t.date) = YEAR(CURRENT_DATE()) " +
            "AND MONTH(t.date) = MONTH(CURRENT_DATE())",
            nativeQuery = true)
    Double getRendaMensalPorUsuario(@Param("userId") UUID userId);
}