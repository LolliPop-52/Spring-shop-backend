package com.example.spring_shop.repository;

import com.example.spring_shop.domain.Order;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    // Используем везде правильное имя "orderDetails"
    @EntityGraph(attributePaths = {"orderDetails", "orderDetails.product"})
    List<Order> findByUserEmail(String email);

    @EntityGraph(attributePaths = {"orderDetails", "pickupPoint"})
    List<Order> findAllByUserEmail(String email);
}
