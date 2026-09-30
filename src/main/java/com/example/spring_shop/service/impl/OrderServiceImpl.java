package com.example.spring_shop.service.impl;


import com.example.spring_shop.domain.*;
import com.example.spring_shop.dto.ActiveOrdersDTO;
import com.example.spring_shop.dto.CreatorNewOrderDTO;
import com.example.spring_shop.dto.OrderDTO;
import com.example.spring_shop.exception_handler.RuntimeException.ProductNotFoundException;
import com.example.spring_shop.exception_handler.RuntimeException.ResourceNotFoundException;
import com.example.spring_shop.exception_handler.RuntimeException.UserNotFoundException;
import com.example.spring_shop.mapper.OrderMapper;
import com.example.spring_shop.repository.*;
import com.example.spring_shop.service.BucketService;
import com.example.spring_shop.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {



    private final PickupPointRepository pickupPointRepository;
    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;


    private final BucketService bucketService;

    private final OrderMapper orderMapper;

    @Override
    @Transactional
    public OrderDTO createOrder(CreatorNewOrderDTO creatorNewOrderDTO) {

        Order newOrder = new Order();

        newOrder.setUser(userRepository.findFirstByEmail(creatorNewOrderDTO.getUserEmail())
                .orElseThrow(() -> new UserNotFoundException(creatorNewOrderDTO.getUserEmail())));
        newOrder.setPickupPoint(pickupPointRepository.findById(creatorNewOrderDTO.getAddressId())
                .orElseThrow(() -> new UserNotFoundException(creatorNewOrderDTO.getUserEmail())));
        newOrder.setPaymentStatus(PaymentStatus.UNPAID);
        newOrder.setDeliveryStatus(DeliveryStatus.PROCESSING);
        
        List<OrderDetails> orderDetailsList = creatorNewOrderDTO.getOrderDetails().stream()
                .map(c -> {
                    Product curProduct = productRepository.findById(c.getProductId())
                            .orElseThrow(() -> new ProductNotFoundException(c.getProductId()));
                    if(curProduct.getPrice().compareTo(c.getPriceOnOrder()) == 0){
                        return OrderDetails.builder()
                                .order(newOrder)
                                .product(curProduct)
                                .amount(c.getAmount())
                                .createdTime(LocalDateTime.now())
                                .totalPrice(curProduct.getPrice().multiply(c.getAmount()))
                                .deliveryStatus(DeliveryStatus.PROCESSING)
                                .paymentType(PaymentType.valueOf(creatorNewOrderDTO.getPaymentType()))
                                .paymentStatus(PaymentStatus.UNPAID)
                                .build();
                    } else {
                        throw new DataIntegrityViolationException("Price mismatch for product");
                    }
                }).toList();

        for (OrderDetails orderDetails : orderDetailsList) {
            newOrder.addDetails(orderDetails);
        }

        bucketService.clearOrderedItems(creatorNewOrderDTO);

        orderRepository.save(newOrder);


        return orderMapper.toDto(newOrder);
    }

    @Override
    @Transactional(readOnly = true)
    public ActiveOrdersDTO getAllActiveOrder(String email) {
        return new ActiveOrdersDTO()
                .toBuilder()
                .orders(
                        orderRepository.findAllByUserEmail(email).stream()
                        .map(orderMapper::toDto).toList())
                .build();
    }
}
