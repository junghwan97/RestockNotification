package com.example.corporatetesk2.repository;

import com.example.corporatetesk2.entity.ProductNotificationHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProductNotificationHistoryRepository extends JpaRepository<ProductNotificationHistory, Long> {
    Optional<ProductNotificationHistory> findTopByProductIdOrderByIdDesc(Long productId);
}
