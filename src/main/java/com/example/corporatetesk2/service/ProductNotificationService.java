package com.example.corporatetesk2.service;

import com.example.corporatetesk2.entity.Product;
import com.example.corporatetesk2.entity.ProductNotificationHistory;
import com.example.corporatetesk2.entity.ProductUserNotification;
import com.example.corporatetesk2.entity.ProductUserNotificationHistory;
import com.example.corporatetesk2.repository.ProductNotificationHistoryRepository;
import com.example.corporatetesk2.repository.ProductRepository;
import com.example.corporatetesk2.repository.ProductUserNotificationHistoryRepository;
import com.example.corporatetesk2.repository.ProductUserNotificationRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductNotificationService {

    private final ProductRepository productRepository;
    private final ProductNotificationHistoryRepository historyRepository;
    private final ProductUserNotificationRepository productUserNotificationRepository;
    private final ProductUserNotificationHistoryRepository userNotificationHistoryRepository;
    private final RateLimiterService rateLimiterService;

    @Transactional
    public void sendRestockNotifications(Long productId) {
        Product product = getProduct(productId);

        product.incrementRestockCount(); // 재입고 시 회차 증가
        product.incrementStock(); // 재입고 시 재고 증가(10개씩)
        productRepository.save(product);

        ProductNotificationHistory history = new ProductNotificationHistory();
        history.setProduct(product);
        history.setRestockCount(product.getRestockCount());
        history.setStatus(ProductNotificationHistory.NotificationStatus.IN_PROGRESS);
        historyRepository.save(history);

        List<ProductUserNotification> users = productUserNotificationRepository.findByProductIdOrderByUserId(productId);

        try {
            sendNotifications(users, product, history);
        } catch (Exception e) {
            history.setStatus(ProductNotificationHistory.NotificationStatus.CANCELED_BY_ERROR);
            throw new RuntimeException("알림 전송 중 에러 발생", e);
        } finally {
            if (!product.isOutOfStock()) history.setStatus(ProductNotificationHistory.NotificationStatus.COMPLETED);
            saveNotificationHistory(history);
        }
    }


    @Transactional
    public void retryRestockNotifications(Long productId) {
        Product product = getProduct(productId);

        ProductNotificationHistory lastHistory = getProductNotificationHistory(productId);

        if (!lastHistory.getStatus().equals(ProductNotificationHistory.NotificationStatus.CANCELED_BY_ERROR)
                && !lastHistory.getStatus().equals(ProductNotificationHistory.NotificationStatus.CANCELED_BY_SOLD_OUT)) {
            throw new IllegalStateException("이전 알림이 오류나 품절로 중단되지 않았습니다.");
        }

        Long lastNotifiedUserId = lastHistory.getLastNotifiedUserId();
        List<ProductUserNotification> users = productUserNotificationRepository.findByProductIdAndUserIdGreaterThanEqualOrderByUserId(productId, lastNotifiedUserId);

        try {
            sendNotifications(users, product, lastHistory);
        } catch (Exception e) {
            lastHistory.setStatus(ProductNotificationHistory.NotificationStatus.CANCELED_BY_ERROR);
            throw new RuntimeException("알림 전송 중 에러 발생", e);
        } finally {
            if (!product.isOutOfStock()) lastHistory.setStatus(ProductNotificationHistory.NotificationStatus.COMPLETED);
            saveNotificationHistory(lastHistory);
        }
    }


    private void sendNotifications(List<ProductUserNotification> users, Product product, ProductNotificationHistory history) {
        for (ProductUserNotification user : users) {
            if (product.isOutOfStock()) {
                history.setStatus(ProductNotificationHistory.NotificationStatus.CANCELED_BY_SOLD_OUT);
                history.setLastNotifiedUserId(user.getUserId());
                saveNotificationHistory(history);
                return;
            }

            rateLimiterService.executeWithRateLimit(() -> {
                ProductUserNotificationHistory userHistory = new ProductUserNotificationHistory();
                userHistory.setProduct(product);
                userHistory.setUserId(user.getUserId());
                userHistory.setRestockCount(product.getRestockCount());
                userHistory.setNotifiedAt(LocalDateTime.now());
                userNotificationHistoryRepository.save(userHistory);

                history.setLastNotifiedUserId(user.getUserId());
                saveNotificationHistory(history);
            });
        }
    }

    // 상품 등록 확인
    private Product getProduct(Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new EntityNotFoundException("등록되지 않은 상품입니다."));
        return product;
    }

    // 재입고 알림 히스토리 조회
    private ProductNotificationHistory getProductNotificationHistory(Long productId) {
        ProductNotificationHistory lastHistory = historyRepository.findTopByProductIdOrderByIdDesc(productId)
                .orElseThrow(() -> new IllegalStateException("재입고 알림 히스토리가 없습니다."));
        return lastHistory;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void saveNotificationHistory(ProductNotificationHistory history) {
        historyRepository.save(history);
    }

}
