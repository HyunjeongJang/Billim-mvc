package com.web.billim.payment.repository;

import java.util.Optional;

import com.web.billim.payment.domain.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Integer> {
    Optional<Payment> findByMerchantUid(String merchantUid);
}
