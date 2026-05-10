package com.example.spring_shop.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.aspectj.weaver.ast.Or;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "orders")
public class Order {

    private final String SEQ_NAME = "order_seq";

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = SEQ_NAME)
    @SequenceGenerator(name = SEQ_NAME, sequenceName = SEQ_NAME, allocationSize = 1)
    private Long id;


    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Builder.Default
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "order", orphanRemoval = true)
    private List<OrderDetails> details = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name = "pickup_point_id", nullable = false)
    private PickupPoint pickupPoint;

    @CreationTimestamp
    private LocalDateTime createdTime;

    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedTime;

    private BigDecimal totalPrice;

    private BigDecimal totalAmount;

    public void addDetails(OrderDetails orderDetails){
        details.add(orderDetails);
        orderDetails.setOrder(this);
        this.totalPrice = (this.totalPrice == null ? BigDecimal.ZERO : this.totalPrice)
                .add(orderDetails.getTotalPrice());
        this.totalAmount = (this.totalAmount == null ? BigDecimal.ZERO : this.totalAmount)
                .add(orderDetails.getAmount());
    }

}
