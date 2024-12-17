package com.example.corporatetesk2.repository;

import com.example.corporatetesk2.entity.ProductUserNotification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface ProductUserNotificationRepository extends JpaRepository<ProductUserNotification, Long> {
    List<ProductUserNotification> findByProductIdOrderByUserId(Long productId);

    List<ProductUserNotification> findByProductIdAndUserIdGreaterThanEqualOrderByUserId(Long productId, Long lastNotifiedUserId);
}

