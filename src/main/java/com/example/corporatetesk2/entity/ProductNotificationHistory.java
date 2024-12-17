package com.example.corporatetesk2.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "product_notification_history",
        indexes = {@Index(name = "idx_product_restock", columnList = "productId, restockCount")})
public class ProductNotificationHistory { // 상품별 알림 발송 상태와 진행 정보를 관리

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "productId", nullable = false)
    private Product product;

    @Column(nullable = false)
    private int restockCount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private NotificationStatus status;

    @Column
    private Long lastNotifiedUserId; // 마지막으로 알림이 발송된 회원 아이디

    public enum NotificationStatus {
        IN_PROGRESS, // 발송중
        COMPLETED, // 완료
        CANCELED_BY_SOLD_OUT, // 품절에 의한 발송 중단
        CANCELED_BY_ERROR // 예외에 의한 발송 중단
    }

}
