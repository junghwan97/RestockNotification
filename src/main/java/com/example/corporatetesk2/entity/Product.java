package com.example.corporatetesk2.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "Product")
public class Product { // 상품 정보, 재입고 회차, 재고 상태 관리

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private int restockCount;

    @Column(nullable = false)
    private int stock;

    public void incrementRestockCount() {
        this.restockCount++;
    }

    public void incrementStock() {
        this.stock += 10;
    }

    public boolean isOutOfStock() {
        return this.stock <= 0;
    }
}
