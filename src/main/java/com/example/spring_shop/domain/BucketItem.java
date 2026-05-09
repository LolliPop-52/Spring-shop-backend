package com.example.spring_shop.domain;


import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;


//@EqualsAndHashCode(of = "id")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "bucket_items")
public class BucketItem {

    private static final String SEQ_NAME = "bucket_items_seq";

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = SEQ_NAME)
    @SequenceGenerator(name = SEQ_NAME, sequenceName = SEQ_NAME, allocationSize = 1)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @ManyToOne
    @JoinColumn(name = "bucket_id")
    private Bucket bucket;

    @Column(nullable = false)
    private BigDecimal amount;

    public BigDecimal getTotalPrice(){
        return amount.multiply(product.getPrice());
    }

}

