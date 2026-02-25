package com.example.guiaFinanceiro.repository;

import com.example.guiaFinanceiro.entites.Invoice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface InvoiceRepository extends JpaRepository<Invoice, UUID> {
    Optional<Invoice> findByCreditCardIdAndReferenceMonth(UUID creditCardId, LocalDate referenceMonth);
}
