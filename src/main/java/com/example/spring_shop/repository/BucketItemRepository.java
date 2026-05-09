package com.example.spring_shop.repository;

import com.example.spring_shop.domain.BucketItem;
import com.example.spring_shop.domain.Product;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BucketItemRepository extends JpaRepository<BucketItem, Long> {

    @EntityGraph(attributePaths = {"product"})
    Optional<BucketItem> findByBucketUserEmailAndProductId(String email, Long productId);
}
