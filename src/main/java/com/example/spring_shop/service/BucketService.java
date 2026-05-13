package com.example.spring_shop.service;

import com.example.spring_shop.dto.BucketDTO;
import com.example.spring_shop.dto.BucketItemDTO;
import com.example.spring_shop.dto.CreatorNewOrderDTO;
import com.example.spring_shop.dto.ModifyBucketItemDTO;

import java.util.List;

public interface BucketService {
    BucketDTO addItemToBucket(ModifyBucketItemDTO newBucketItemDTO);

    BucketDTO deleteItemOnBucket(ModifyBucketItemDTO newBucketItemDTO);


    BucketDTO clearOrderedItems(CreatorNewOrderDTO creatorNewOrderDTO);

    //void removeOrderedProduct(List<BucketItemDTO> items);

    List<BucketItemDTO> takeAllItem(String email);

    BucketDTO clearBucket(String email);

    BucketDTO getBucketByUser(String email);
}
