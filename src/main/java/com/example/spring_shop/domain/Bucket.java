package com.example.spring_shop.domain;

import java.util.List;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


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
    
    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(name = "buckets_products", 
        joinColumns = @JoinColumn(name = "bucket_id"),
        inverseJoinColumns = @JoinColumn(name = "product_id"))
    private List<Product> products;

}
