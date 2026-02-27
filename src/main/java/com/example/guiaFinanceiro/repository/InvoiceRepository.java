package com.example.guiaFinanceiro.repository;

import com.example.guiaFinanceiro.entites.Account;
import com.example.guiaFinanceiro.entites.CreditCard;
import com.example.guiaFinanceiro.entites.Invoice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface InvoiceRepository extends JpaRepository<Invoice, UUID> {
    Optional<Invoice> findByCreditCardIdAndReferenceMonth(UUID creditCardId, LocalDate referenceMonth);
    Optional<Invoice> findByCreditCardId(UUID creditCardId);

    boolean existsByCreditCardAndReferenceMonth(CreditCard creditCard, LocalDate referenceMonth);

    @Query(value = "SELECT i.* FROM INVOICE i " +
            "INNER JOIN CREDIT_CARD cc ON i.CREDIT_CARD_ID = cc.ID " +
            "WHERE cc.USERS_ID = :userId", nativeQuery = true)
    List<Invoice> findByUsers_Id(@Param("userId") UUID userId);

    Optional<Invoice> findByCreditCardIdAndPaidFalse(UUID creditCardId);
}
