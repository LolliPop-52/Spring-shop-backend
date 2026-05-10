package com.example.spring_shop.domain;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.transaction.annotation.Transactional;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "buckets")
public class Bucket {

    private static final String SEQ_NAME = "bucket_seq";

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = SEQ_NAME)
    @SequenceGenerator(name = SEQ_NAME, sequenceName = SEQ_NAME, allocationSize = 1)
    private Long id; 

    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;


    @Builder.Default
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "bucket", orphanRemoval = true)
    @OrderBy("id DESC")
    private List<BucketItem> items = new ArrayList<>();

    public void addItem(BucketItem bucketItem){
        this.items.add(bucketItem);
        bucketItem.setBucket(this);
    }

    public void deleteItem(BucketItem bucketItem){
        this.items.remove(bucketItem);
        bucketItem.setBucket(null);
    }

    public BigDecimal getItemsAmount(){
        return items.stream()
                .map(BucketItem::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public BigDecimal getTotalPrice(){
        return items.stream()
                .map(BucketItem::getTotalPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
