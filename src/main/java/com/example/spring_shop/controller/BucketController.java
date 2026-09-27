package com.example.spring_shop.controller;

import com.example.spring_shop.dto.BucketDTO;
import com.example.spring_shop.dto.ModifyBucketItemDTO;
import com.example.spring_shop.security.CustomUserDetails;
import com.example.spring_shop.service.BucketService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;


@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/bucket")
public class BucketController {

    private final BucketService bucketService;

    @GetMapping()
    public ResponseEntity<BucketDTO> getBucket(@AuthenticationPrincipal CustomUserDetails userDetails){
        return new ResponseEntity<>(bucketService.getBucketByUser(userDetails.getUsername()), HttpStatus.OK);
    }

    @PostMapping()
    public ResponseEntity<BucketDTO> addBucketItem(@AuthenticationPrincipal CustomUserDetails userDetails,
                                                   @RequestBody ModifyBucketItemDTO newBucketItemDTO){
        newBucketItemDTO.setUserEmail(userDetails.getUsername());
        return new ResponseEntity<>(bucketService.addItemToBucket(newBucketItemDTO), HttpStatus.CREATED);
    }

    @PatchMapping()
    public ResponseEntity<BucketDTO> deleteBucketItem(@AuthenticationPrincipal CustomUserDetails userDetails,
                                                      @RequestBody ModifyBucketItemDTO deleteBucketItemDTO) {
        deleteBucketItemDTO.setUserEmail(userDetails.getUsername());
        return new ResponseEntity<>(bucketService.deleteItemOnBucket(deleteBucketItemDTO), HttpStatus.OK);
    }

    @PutMapping("/clear")
    public ResponseEntity<BucketDTO> clear(@AuthenticationPrincipal CustomUserDetails userDetails) {
        return new ResponseEntity<>(bucketService.clearBucket(userDetails.getUsername()), HttpStatus.OK);
    }

    @GetMapping("/amount")
    public ResponseEntity<BigDecimal> getAmountOfItems(@AuthenticationPrincipal CustomUserDetails userDetails) {
        BigDecimal totalAmount = bucketService.getBucketByUser(userDetails.getUsername()).getTotalItemsAmount();
        return new ResponseEntity<>(totalAmount, HttpStatus.OK);
    }
}
