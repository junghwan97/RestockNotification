package com.example.corporatetesk2.controller;

import com.example.corporatetesk2.service.ProductNotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class ProductNotificationController {

    private final ProductNotificationService productNotificationService;

    // 재입고 알림 전송
    @PostMapping("/products/{productId}/notifications/re-stock")
    public ResponseEntity<String> sendRestockNotification(@PathVariable Long productId) {
        productNotificationService.sendRestockNotifications(productId);
        return ResponseEntity.ok("Notifications sent successfully");
    }

    // 알림 전송 실패 유저에게 다시 알림 재전송
    @PostMapping("/admin/products/{productId}/notifications/re-stock")
    public ResponseEntity<Void> manualSendRestockNotifications(@PathVariable Long productId) {
        productNotificationService.retryRestockNotifications(productId);
        return ResponseEntity.ok().build();
    }

}
