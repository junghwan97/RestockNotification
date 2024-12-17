package com.example.corporatetesk2.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Table(name = "ProductUserNotificationHistory")
public class ProductUserNotificationHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "productId", nullable = false)
    private Product product;

    @Column(nullable = false)
    private Long userId;

    @Column(nullable = false)
    private int restockCount;

    @Column(nullable = false)
    private LocalDateTime notifiedAt;

    @PrePersist
    public void prePersist() {
        notifiedAt = LocalDateTime.now();
    }
}

