package com.example.spring_shop.controller;


import com.example.spring_shop.dto.*;
import com.example.spring_shop.security.CustomUserDetails;
import com.example.spring_shop.service.OrderService;
import com.example.spring_shop.service.PickupPointService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedModel;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;


@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/order")
public class OrderController {


    private final OrderService orderService;
    private final PickupPointService pickupPointService;

    @PostMapping("/new")
    public ResponseEntity<OrderDTO> newOrder(@AuthenticationPrincipal CustomUserDetails userDetails,
                                             @RequestBody CreatorNewOrderDTO creatorNewOrderDTO) {
        creatorNewOrderDTO.setUserEmail(userDetails.getUsername());
        return ResponseEntity.ok(orderService.createOrder(creatorNewOrderDTO));
    }

    @GetMapping("/orders")
    public ResponseEntity<ActiveOrdersDTO> getMyOrders(@AuthenticationPrincipal CustomUserDetails userDetails) {
        return ResponseEntity.ok(orderService.getAllActiveOrder(userDetails.getUsername()));
    }

    @GetMapping("/pickup-points")
    public ResponseEntity<PagedModel<PickupPointDTO>> getAllPickupPoints(Pageable pageable) {
        return ResponseEntity.ok(new PagedModel<>(
                pickupPointService.findAllPickupPoints(pageable)));
    }
}
