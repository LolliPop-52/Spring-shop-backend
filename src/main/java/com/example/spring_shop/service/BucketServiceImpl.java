package com.example.spring_shop.service;

import com.example.spring_shop.domain.Bucket;
import com.example.spring_shop.domain.BucketItem;
import com.example.spring_shop.domain.Product;
import com.example.spring_shop.dto.*;
import com.example.spring_shop.exception_handler.ResourceNotFoundException;
import com.example.spring_shop.mapper.BucketItemMapper;
import com.example.spring_shop.mapper.BucketMapper;
import com.example.spring_shop.repository.BucketItemRepository;
import com.example.spring_shop.repository.BucketRepository;
import com.example.spring_shop.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class BucketServiceImpl implements BucketService{


    private final BucketMapper bucketMapper;
    private final BucketItemMapper bucketItemMapper;

    private final ProductRepository productRepository;
    private final BucketRepository bucketRepository;
    private final BucketItemRepository bucketItemRepository;



    @Override
    @Transactional
    public BucketDTO addItemToBucket(ModifyBucketItemDTO newBucketItemDTO) {

        // amount > 0
        if (newBucketItemDTO.getAmount().compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Amount must be positive");
        }

        Bucket bucket = getBucketEntityByUser(newBucketItemDTO.getUserEmail());

        Optional<BucketItem> existingItem = getBucketItemEntityOnBucketByProductId(bucket, newBucketItemDTO.getProductId());

        if(existingItem.isEmpty()){

            Product newProduct = productRepository.findById(newBucketItemDTO.getProductId())
                    .orElseThrow(() -> new ResourceNotFoundException(newBucketItemDTO.getProductId()));


            bucket.addItem(
                    BucketItem.builder()
                           .amount(newBucketItemDTO.getAmount())
                           .product(newProduct)
                           .build()
           );
        } else {
            BucketItem bucketItem = existingItem.get();

            BigDecimal newAmount = bucketItem.getAmount().add(newBucketItemDTO.getAmount());

            bucketItem.setAmount(newAmount);
        }
        bucketRepository.save(bucket);
        return bucketMapper.toDto(bucket);
    }

    @Override
    @Transactional
    public BucketDTO deleteItemOnBucket(ModifyBucketItemDTO deleteBucketItemDTO) {
        Bucket bucket = getBucketEntityByUser(deleteBucketItemDTO.getUserEmail());


        BucketItem bucketItem = getBucketItemEntityOnBucketByProductId(bucket, deleteBucketItemDTO.getProductId())
                .orElseThrow(() -> new ResourceNotFoundException(deleteBucketItemDTO.getProductId()));

        BigDecimal amountToDelete = deleteBucketItemDTO.getAmount().abs();

        BigDecimal newAmount = bucketItem.getAmount().subtract(amountToDelete);
        // newAmount < 1
        if(newAmount.compareTo(BigDecimal.ONE) < 0) {
            bucket.deleteItem(bucketItem);
        } else {
            bucketItem.setAmount(newAmount);
        }
        bucketRepository.save(bucket);
        return bucketMapper.toDto(bucket);
    }

    @Override
    @Transactional(readOnly = true)
    public List<BucketItemDTO> takeAllItem(String email) {
        Bucket bucket = getBucketEntityByUser(email);
        return bucket.getItems().stream()
                .map(bucketItemMapper::toDto)
                .toList();
    }

    @Override
    @Transactional
    public BucketDTO clearBucket(String email) {
        Bucket bucket = getBucketEntityByUser(email);
        bucket.getItems().clear();
        bucketRepository.save(bucket);
        return bucketMapper.toDto(bucket);
    }

    @Override
    @Transactional
    public BucketDTO clearOrderedItems(CreatorNewOrderDTO creatorNewOrderDTO){
        Bucket bucket = bucketRepository.findByUserEmail(creatorNewOrderDTO.getUserEmail())
                .orElseThrow(() -> new ResourceNotFoundException(creatorNewOrderDTO.getUserEmail()));
        for(CreatorNewOrderDetailsDTO orderDetail : creatorNewOrderDTO.getOrderDetails()){
            deleteItemOnBucket(ModifyBucketItemDTO.builder()
                    .userEmail(creatorNewOrderDTO.getUserEmail())
                    .productId(orderDetail.getProductId())
                    .amount(orderDetail.getAmount())
                    .build());
        }
        return bucketMapper.toDto(bucket);
    }

    @Override
    @Transactional(readOnly = true)
    public BucketDTO getBucketByUser(String email) {
        return bucketMapper.toDto(getBucketEntityByUser(email));
    }


    private Bucket getBucketEntityByUser(String email) {
        return bucketRepository.findByUserEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException(email));
    }

    private Optional<BucketItem> getBucketItemEntityOnBucketByProductId(Bucket bucket, Long id){
        return bucket.getItems().stream()
                .filter(bucketItem -> bucketItem.getProduct().getId()
                        .equals(id))
                .findFirst();
    }
}
