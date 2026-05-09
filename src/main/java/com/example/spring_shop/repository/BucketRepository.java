package com.example.spring_shop.repository;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.example.spring_shop.domain.Bucket;

import java.util.List;
import java.util.Optional;

@Repository
public interface BucketRepository extends JpaRepository<Bucket, Long>{
    @EntityGraph(attributePaths = {"items", "items.product"})
    Optional<Bucket> findByUserEmail(String email);

}
