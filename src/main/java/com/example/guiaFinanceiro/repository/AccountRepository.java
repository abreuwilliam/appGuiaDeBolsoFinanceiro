package com.example.guiaFinanceiro.repository;

import com.example.guiaFinanceiro.entites.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Repository
public interface AccountRepository extends JpaRepository<Account, UUID> {

    @Query(value = "SELECT COALESCE(SUM(balance), 0) FROM account WHERE users_id = :userId", nativeQuery = true)
    BigDecimal sumBalanceByUserId(@Param("userId") UUID userId);

    List<Account> findByUsers_Id(UUID usersId);


}
