package com.example.spring_shop.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.example.spring_shop.domain.Bucket;

@Repository
public interface BucketRepository extends JpaRepository<Bucket, Long>{
    
}
