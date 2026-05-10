package com.example.spring_shop.controller;

import com.example.spring_shop.dto.BucketDTO;
import com.example.spring_shop.dto.ModifyBucketItemDTO;
import com.example.spring_shop.security.CustomUserDetails;
import com.example.spring_shop.service.BucketService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;


@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/bucket")
public class BucketController {

    private final BucketService bucketService;

    @GetMapping()
    public ResponseEntity<BucketDTO> getBucket(@AuthenticationPrincipal CustomUserDetails userDetails){
        return ResponseEntity.ok(bucketService.getBucketByUser(userDetails.getUsername()));
    }

    @PostMapping()
    public ResponseEntity<BucketDTO> addBucketItem(@AuthenticationPrincipal CustomUserDetails userDetails,
                                                   @RequestBody ModifyBucketItemDTO newBucketItemDTO){
        newBucketItemDTO.setUserEmail(userDetails.getUsername());
        System.out.println(newBucketItemDTO);
        return ResponseEntity.ok(bucketService.addItemToBucket(newBucketItemDTO));
    }

    @DeleteMapping()
    public ResponseEntity<BucketDTO> deleteBucketItem(@AuthenticationPrincipal CustomUserDetails userDetails,
                                                      @RequestBody ModifyBucketItemDTO deleteBucketItemDTO) {
        deleteBucketItemDTO.setUserEmail(userDetails.getUsername());
        return ResponseEntity.ok(bucketService.deleteItemOnBucket(deleteBucketItemDTO));
    }

    @DeleteMapping("/clear")
    public ResponseEntity<BucketDTO> clear(@AuthenticationPrincipal CustomUserDetails userDetails) {
        return ResponseEntity.ok(bucketService.clearBucket(userDetails.getUsername()));
    }
}
